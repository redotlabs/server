package redot.redot_server.domain.cms.inquiry.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquiryCreateRequest;

/** Opt-in closed-loop service benchmark. No HTTP, artificial delays, or production data. */
@Tag("experiment")
class InquiryLoadExperiment extends InquiryConcurrencyTest {
    record Result(int id, String number, double millis, String error) {}

    @Test
    void measure() throws Exception {
        String variant = System.getProperty("experiment.variant");
        Path output = Path.of(System.getProperty("experiment.output"));
        Files.createDirectories(output);
        Files.writeString(output.resolve("environment.txt"), "variant=" + variant
                + "\njava=" + System.getProperty("java.version") + "\nos=" + System.getProperty("os.name")
                + "\narch=" + System.getProperty("os.arch") + "\npostgres="
                + jdbc.queryForObject("SELECT version()", String.class)
                + "\nisolation=" + jdbc.queryForObject("SHOW transaction_isolation", String.class)
                + "\npool=16\nrequests_per_cell=1000\nrepetitions=3\nwarmup_per_cell=100 sequential\n");
        try (var summary = Files.newBufferedWriter(output.resolve("summary.csv"));
             var raw = Files.newBufferedWriter(output.resolve("requests.csv"));
             var samples = Files.newBufferedWriter(output.resolve("samples.csv"))) {
            summary.write("variant,repeat,initial,workers,requests,success,failed,committed,unique,final_sequence,seconds,success_per_second,p50_ms,p95_ms,p99_ms,success_p95_ms,max_active,max_pending,max_db_lock_waiters,db_cpu_seconds\n");
            raw.write("variant,repeat,initial,workers,request_id,success,number,millis,error\n");
            samples.write("variant,repeat,initial,workers,elapsed_ms,active,pending,db_lock_waiters\n");
            for (int repeat = 1; repeat <= 3; repeat++) {
                // Reverse the order on the second repetition to expose order/warmup effects.
                int[] levels = repeat == 2 ? new int[]{100,32,8,1} : new int[]{1,8,32,100};
                for (int initial : new int[]{0,40}) for (int workers : levels) {
                    createTenants();
                    for (int i = 0; i < 100; i++) service.createInquiry(1L,
                            new RedotAppInquiryCreateRequest("Warmup", "Warmup", "Content"));
                    createTenants();
                    if (initial > 0) jdbc.update("INSERT INTO inquiry_sequences (inquiry_date, last_sequence_number) VALUES (?, ?)", LocalDate.now(clock), initial);
                    var pool = ((HikariDataSource) jdbc.getDataSource()).getHikariPoolMXBean();
                    var done = new AtomicBoolean();
                    var ready = new CountDownLatch(workers);
                    var start = new CountDownLatch(1);
                    var index = new AtomicInteger();
                    int[] maxima = new int[3];
                    long[] began = new long[1];
                    List<Result> results = new ArrayList<>();
                    long cpuBefore = cpuMicros();
                    long elapsed;
                    try (var observer = DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
                         var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                        var monitor = executor.submit(() -> {
                            start.await(10, TimeUnit.SECONDS);
                            while (!done.get()) {
                                int active = pool.getActiveConnections();
                                int pending = pool.getThreadsAwaitingConnection();
                                int waiting;
                                try (var st = observer.createStatement(); var rs = st.executeQuery("SELECT count(*) FROM pg_stat_activity WHERE datname=current_database() AND wait_event_type='Lock'")) {
                                    rs.next(); waiting = rs.getInt(1);
                                }
                                maxima[0] = Math.max(maxima[0], active);
                                maxima[1] = Math.max(maxima[1], pending);
                                maxima[2] = Math.max(maxima[2], waiting);
                                samples.write(String.format(Locale.ROOT, "%s,%d,%d,%d,%.3f,%d,%d,%d%n", variant,
                                        experimentRepeat, experimentInitial, experimentWorkers,
                                        (System.nanoTime()-began[0])/1e6, active, pending, waiting));
                                Thread.sleep(10);
                            }
                            return null;
                        });
                        List<Future<List<Result>>> futures = new ArrayList<>();
                        experimentRepeat=repeat; experimentInitial=initial; experimentWorkers=workers;
                        for (int w = 0; w < workers; w++) futures.add(executor.submit(() -> {
                            List<Result> local = new ArrayList<>();
                            ready.countDown();
                            if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Start timeout");
                            int id;
                            while ((id=index.getAndIncrement()) < 1000) {
                                long t = System.nanoTime();
                                String number = ""; String error = "";
                                try { number=service.createInquiry(id%2+1L,
                                        new RedotAppInquiryCreateRequest("Load", "request-"+id, "Content")).inquiryNumber(); }
                                catch (Exception ex) { error=ex.getClass().getSimpleName(); }
                                local.add(new Result(id, number, (System.nanoTime()-t)/1e6, error));
                            }
                            return local;
                        }));
                        try {
                            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
                            began[0]=System.nanoTime(); start.countDown();
                            for (var f:futures) results.addAll(f.get(60, TimeUnit.SECONDS));
                            elapsed=System.nanoTime()-began[0];
                        } finally { start.countDown(); done.set(true); }
                        monitor.get(10, TimeUnit.SECONDS);
                    }
                    double cpuSeconds=(cpuMicros()-cpuBefore)/1e6;
                    long success=results.stream().filter(r->r.error().isEmpty()).count();
                    long committed=jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries",Long.class);
                    long unique=jdbc.queryForObject("SELECT count(DISTINCT inquiry_number) FROM redot_app_inquiries",Long.class);
                    long last=jdbc.queryForObject("SELECT coalesce(max(last_sequence_number),0) FROM inquiry_sequences",Long.class);
                    assertThat(results).hasSize(1000);
                    assertThat(committed).isEqualTo(success);
                    assertThat(unique).isEqualTo(committed);
                    assertThat(last).isEqualTo(initial+committed);
                    for (Result r:results) {
                        long persisted=jdbc.queryForObject("SELECT count(*) FROM redot_app_inquiries WHERE title=? AND inquiry_number=?",Long.class,"request-"+r.id(),r.number());
                        assertThat(persisted).isEqualTo(r.error().isEmpty()?1L:0L);
                        raw.write(String.format(Locale.ROOT,"%s,%d,%d,%d,%d,%s,%s,%.3f,%s%n",variant,repeat,initial,workers,r.id(),r.error().isEmpty(),r.number(),r.millis(),r.error()));
                    }
                    if ((!variant.equals("unlocked") && initial > 0) || variant.equals("fixed")) assertThat(success).isEqualTo(1000L);
                    double[] times=results.stream().mapToDouble(Result::millis).sorted().toArray();
                    double[] successes=results.stream().filter(r->r.error().isEmpty()).mapToDouble(Result::millis).sorted().toArray();
                    double seconds=elapsed/1e9;
                    summary.write(String.format(Locale.ROOT,"%s,%d,%d,%d,1000,%d,%d,%d,%d,%d,%.6f,%.3f,%.3f,%.3f,%.3f,%.3f,%d,%d,%d,%.6f%n",variant,repeat,initial,workers,success,1000-success,committed,unique,last,seconds,success/seconds,p(times,.50),p(times,.95),p(times,.99),p(successes,.95),maxima[0],maxima[1],maxima[2],cpuSeconds));
                    summary.flush(); raw.flush(); samples.flush();
                }
            }
        }
    }

    private int experimentRepeat, experimentInitial, experimentWorkers;
    private static double p(double[] sorted,double percentile) {
        return sorted.length==0?Double.NaN:sorted[(int)Math.ceil(percentile*sorted.length)-1];
    }
    private static long cpuMicros() throws Exception {
        var result=POSTGRES.execInContainer("cat","/sys/fs/cgroup/cpu.stat");
        if(result.getExitCode()!=0) throw new IllegalStateException("cgroup v2 CPU accounting unavailable");
        return result.getStdout().lines().filter(l->l.startsWith("usage_usec "))
                .mapToLong(l->Long.parseLong(l.split(" ")[1])).findFirst().orElseThrow();
    }
}
