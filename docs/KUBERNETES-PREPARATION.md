# Kubernetes 배포 준비 (로컬 검증 완료, commit/push 대기)

기준: main c8fa281f40c3cb02b107b9e30a10e84f69b1df90. namespace `redot`, API `redot-api.dhxxn.kr`. 아직 commit/push/K3s 배포하지 않았다. 기존 production 운영값은 읽지 않는다. 신규 DB이므로 사용자 후속 지시에 따라 과거 서버 버전 추적을 중단하고 PostgreSQL 17.11을 선정했다.

## 기존 production과 분리

기존 `Dockerfile`, Compose, EC2/develop workflow, production profile Secret은 이번에 수정하지 않는다. `SPRING_PROFILES_ACTIVE=homelab`을 사용하는 새 경로만 준비한다. profile group이 prod CookieProvider를 선택하되 Kubernetes JAR에는 기존 주입 application-prod.yml 자체를 넣지 않는다. 기존 prod/dev에서는 새 Actuator 공개가 기본 차단된다. 기존 API SecurityFilterChain과 OAuth/비즈니스 로직/SQL은 수정하지 않았다.

main과 develop 차이는 CookieProviderDev의 헤더 로그 제거뿐이다. main+dev profile은 request header를 기록할 수 있으므로 홈랩에서는 dev를 활성화하지 않는다. 해당 코드 제거는 별도 변경으로 남겼다. PostgreSQL 버전 전체 이력 조사에서 발견한 1.20.1/42.7.3은 각각 Testcontainers/JDBC 라이브러리 버전이며 서버 버전 근거가 아니다.

## 새 빌드 경로

Java 21과 Gradle wrapper 8.14.3을 사용한다. wrapper distribution SHA-256을 고정했다.

```bash
./gradlew test  # 외부 DB 없는 테스트
python3 scripts/test-kubernetes.py  # 실제 임시 DB/Redis + 모든 테스트 + kubernetesBootJar
# 위 검증이 모두 통과한 후에만 실제 배포용 image 발행 단계로 진행
# 아래는 로컬 빌드 예시이며 push 명령이 아님
docker build --platform linux/amd64 -f Dockerfile.kubernetes -t redot-server:local-validation .
```

`kubernetesBootJar`는 `build/kubernetes/app.jar`를 만든다. main class와 classpath를 명시하고 기존 processResources 출력 대신 allowlist로 모은 `application.yml`, `application-homelab.yml`, `db/migration/*.sql`만 포함한다. legacy 주입 profile, .env, 기타 임의 resource를 포함하지 않는다. 향후 앱에 새 정적 resource가 필요하면 검토 후 allowlist에 추가한다. archive 순서와 timestamp를 고정했지만 supply-chain attestation/모든 dependency 장기 재현성을 보장하는 의미는 아니다.

Kubernetes 전용 Dockerfile은 amd64 Java21 JRE manifest digest를 고정하고 UID/GID10001, 명시적인 `/app/app.jar`, exec-form Java 실행을 사용한다. build stage를 추가하지 않았다. Dockerfile별 .dockerignore는 해당 JAR와 Dockerfile만 context에 허용한다. 기존 CI의 build→JAR→image 구조를 유지하면서 분리했다. 현재 기존 workflow는 여전히 legacy profile을 image에 포함하므로 **그 image를 홈랩에 재사용하지 않는다**. 신규 image 발행 전에 workflow의 새 경로 전환/테스트/기존 배포 trigger 분리를 승인받아야 한다.

credential은 image ARG/ENV/JVM option에 넣지 않는다. 런타임 Kubernetes SOPS Secret에서 주입한다. `JAVA_TOOL_OPTIONS`는 JVM이 로그에 출력할 수 있으므로 memory/timezone 등 비민감 옵션에만 사용한다.

## 런타임 변수 계약

아래는 실제 추가한 `application-homelab.yml`의 placeholder 전부다. 값은 저장하지 않았다. `${...}`가 필수인 항목은 사용자 안전 입력/설정 확정 전 정상 애플리케이션 startup을 보장하지 않는다. PostgreSQL image는 docs/runtime-images.json에 17.11과 digest를 고정했다.

| 환경변수 | Spring property | 필수/기본 | Kubernetes 관리 |
|---|---|---|---|
| `SERVER_PORT` | `server.port` | 선택 (기본 8080) | ConfigMap |
| `FORWARD_HEADERS_STRATEGY` | `server.forward-headers-strategy` | 선택 (기본 framework) | ConfigMap |
| `DB_HOST` | `spring.datasource.url` | 필수 | ConfigMap |
| `DB_PORT` | `spring.datasource.url` | 선택 (기본 5432) | ConfigMap |
| `DB_NAME` | `spring.datasource.url` | 필수 | ConfigMap |
| `DB_USERNAME` | `spring.datasource.username` | 필수 | ConfigMap |
| `DB_PASSWORD` | `spring.datasource.password` | 필수 | SOPS Secret |
| `DB_POOL_SIZE` | `spring.datasource.hikari.maximum-pool-size` | 선택 (기본 5) | ConfigMap |
| `REDIS_HOST` | `spring.data.redis.host` | 필수 | ConfigMap |
| `REDIS_PORT` | `spring.data.redis.port` | 선택 (기본 6379) | ConfigMap |
| `REDIS_PASSWORD` | `spring.data.redis.password` | 필수 | SOPS Secret |
| `SMTP_HOST` | `spring.mail.host` | 필수 | ConfigMap |
| `SMTP_PORT` | `spring.mail.port` | 선택 (기본 587) | ConfigMap |
| `SMTP_USERNAME` | `spring.mail.username` | 필수 | SOPS Secret |
| `SMTP_PASSWORD` | `spring.mail.password` | 필수 | SOPS Secret |
| `SMTP_AUTH` | `spring.mail.properties.mail.smtp.auth` | 선택 (기본 true) | ConfigMap |
| `SMTP_STARTTLS_ENABLE` | `spring.mail.properties.mail.smtp.starttls.enable` | 선택 (기본 true) | ConfigMap |
| `SMTP_STARTTLS_REQUIRED` | `spring.mail.properties.mail.smtp.starttls.required` | 선택 (기본 true) | ConfigMap |
| `SMTP_SSL_ENABLE` | `spring.mail.properties.mail.smtp.ssl.enable` | 선택 (기본 false) | ConfigMap |
| `OAUTH2_BASE_URL` | `spring.security.oauth2.base-url` | 선택 (기본 https://redot-api.dhxxn.kr) | ConfigMap |
| `GOOGLE_CLIENT_ID` | `spring.security.oauth2.client.registration.redot-member-google.client-id` | 필수 | SOPS Secret |
| `GOOGLE_CLIENT_SECRET` | `spring.security.oauth2.client.registration.redot-member-google.client-secret` | 필수 | SOPS Secret |
| `GOOGLE_REDIRECT_URI` | `spring.security.oauth2.client.registration.redot-member-google.redirect-uri` | 필수 | ConfigMap |
| `GOOGLE_SCOPES` | `spring.security.oauth2.client.registration.redot-member-google.scope` | 선택 (기본 openid,profile,email) | ConfigMap |
| `JWT_SECRET` | `jwt.secret` | 필수 | SOPS Secret |
| `JWT_COOKIE_DOMAIN` | `jwt.cookie.domain` | 선택 (기본 redot-api.dhxxn.kr) | ConfigMap |
| `JWT_COOKIE_SAME_SITE` | `jwt.cookie.same-site` | 선택 (기본 Lax) | ConfigMap |
| `AWS_REGION` | `cloud.aws.region.static` | 필수 | ConfigMap |
| `AWS_ACCESS_KEY_ID` | `cloud.aws.credentials.access-key` | 필수 | SOPS Secret |
| `AWS_SECRET_ACCESS_KEY` | `cloud.aws.credentials.secret-key` | 필수 | SOPS Secret |
| `S3_BUCKET` | `cloud.aws.s3.bucket` | 필수 | ConfigMap |
| `IMAGE_CDN_BASE_URL` | `image.cdn-base-url` | 선택 (기본 빈 값) | ConfigMap |
| `IMAGE_MAX_SIZE_BYTES` | `image.upload.max-size-bytes` | 선택 (기본 5242880) | ConfigMap |
| `IMAGE_ALLOWED_CONTENT_TYPES` | `image.upload.allowed-content-types` | 선택 (기본 image/*) | ConfigMap |
| `MAIL_FROM` | `email.verification.from-address` | 필수 | ConfigMap |
| `MAIL_BRAND_NAME` | `email.verification.brand-name` | 선택 (기본 Redot) | ConfigMap |
| `EMAIL_CODE_LENGTH` | `email.verification.code-length` | 선택 (기본 6) | ConfigMap |
| `EMAIL_CODE_TTL_SECONDS` | `email.verification.code-ttl-seconds` | 선택 (기본 300) | ConfigMap |
| `EMAIL_TOKEN_TTL_SECONDS` | `email.verification.token-ttl-seconds` | 선택 (기본 900) | ConfigMap |
| `EMAIL_RESEND_COOLDOWN_SECONDS` | `email.verification.resend-cooldown-seconds` | 선택 (기본 5) | ConfigMap |
| `AUTH_SUCCESS_REDIRECT` | `auth.flows.redot-member.default-success-redirect` | 필수 | ConfigMap |
| `AUTH_FAILURE_REDIRECT` | `auth.flows.redot-member.default-failure-redirect` | 필수 | ConfigMap |
| `AUTH_ALLOWED_REDIRECT_HOSTS` | `auth.flows.redot-member.allowed-redirect-hosts` | 필수 | ConfigMap |
| `ADMIN_CONSOLE_BASE_URL` | `notifications.consultation.admin-console-base-url` | 선택 (기본 빈 값) | ConfigMap |
| `DISCORD_WEBHOOK_URL` | `notifications.consultation.discord.webhook-url` | 선택 (기본 빈 값) | SOPS Secret |
| `CORS_ALLOWED_ORIGINS` | `redot.cors.allowed-origins` | 필수 | ConfigMap |
| `LOG_LEVEL` | `logging.level.root` | 선택 (기본 INFO) | ConfigMap |

추가 환경변수: `SPRING_PROFILES_ACTIVE=homelab`, `JAVA_TOOL_OPTIONS`(선택: 초기 `-Xmx512m -Duser.timezone=UTC` 후보), `TZ=UTC`는 비민감 ConfigMap 항목이다. 표의 DB_HOST/PORT/NAME/USERNAME과 DB_PASSWORD는 정확한 JDBC property로 연결된다. 표준 Spring 환경변수로 property 전체를 override할 수도 있지만 상충하는 입력을 중복 지정하지 않는다.

OAuth Google client ID는 공개 식별자지만 credential 묶음으로 SOPS 관리한다. DB_USERNAME은 role 식별자여서 ConfigMap 가능하며 password는 반드시 Secret이다. POSTGRES_* 초기화 값은 Backend 변수와 별개이며 후속 DB manifest 단계에서 설계한다. Backend에 PostgreSQL bootstrap superuser를 제공하지 않는다. Redis server password 설정도 이 profile 밖에서 별도 설계한다.

## Health / Security / 종료

- GET `/actuator/health`, `/actuator/health/liveness`, `/actuator/health/readiness` 세 경로만 homelab에서 익명 허용한다. `/actuator/health/db` 등 기타 하위 경로와 전체 actuator API는 deny한다.
- web 노출은 health만, details/components는 never. management endpoints default access none + health read-only다. env/configprops/heapdump를 추가 노출하더라도 security chain이 거부하는 테스트를 포함했다.
- liveness group은 **livenessState만** 포함한다. DB/Redis가 내려가도 재시작 loop를 유발하지 않는다.
- readiness는 readinessState+db+redis다. 인증/핵심 데이터 서비스가 정상일 때만 요청을 받도록 선택했다. SMTP/S3/Google/Discord는 포함하지 않는다.
- probes는 현재 8080의 실제 application HTTP server를 검사하도록 설계했다. 내부 probe 접근을 열었다고 health가 인터넷에서 절대 접근 불가한 것은 아니다. 후속 HTTPRoute는 필요한 API 경로만 노출하고 `/actuator`를 public routing하지 않아야 한다. 아직 Route는 생성하지 않았다.
- startupProbe: liveness 경로, period5s/timeout2s/failure60 후보. liveness:10s/2s/3, readiness:5s/2s/3. 실제 초기 migration 시간을 관찰해 조정한다.
- graceful shutdown 활성화, Spring phase30s. 향후 terminationGracePeriodSeconds45s 후보. 실제 SIGTERM/트래픽 drain은 배포 전 통합 검증 대상이다.
- 기존 API 인증체계는 그대로이며 new chain은 /actuator 범위만 우선 처리한다. homeLab CORS Filter는 profile 한정으로 API 경로만 처리한다.

## CORS / OAuth / Cookie

`CORS_ALLOWED_ORIGINS`는 실제 frontend origin을 쉼표로 나눈 명시 목록이며 wildcard/경로 포함 origin은 시작 시 거부한다. credential 요청을 허용하고 methods GET/POST/PUT/PATCH/DELETE/OPTIONS, headers Authorization/Content-Type/Accept/X-App-Subdomain만 허용한다. prod/dev에는 새 CORS filter를 등록하지 않는다. 이 변경은 기존 CSRF/authentication 정책을 재설계하지 않는다.

prod CookieProvider를 재사용하고 cookie Secure=true, domain 기본 redot-api.dhxxn.kr, SameSite 기본 Lax로 override한다. frontend가 다른 site(redot.me 등)면 Lax로 fetch 인증이 동작하지 않을 수 있으므로 실제 origin을 받고 SameSite=None/브라우저 정책을 검증해야 한다. callback/redirect는 필수 입력이며 가짜 frontend URL을 기본값으로 넣지 않았다.

OAuth base URL 기본값만 https://redot-api.dhxxn.kr이다. GOOGLE_REDIRECT_URI는 실제 Google console와 일치해야 한다. 현재 FlowAwareRedirectUriResolver는 frontend redirect_uri에 따라 API callback host를 바꿀 수 있다. 이를 이번에 변경하지 않았으며 frontend reverse proxy 유무에 따라 별도 인증 흐름 검증이 필요하다. Swagger는 homelab에서 api-docs/UI 모두 비활성화한다. 기존 !prod Swagger URL이나 production 도메인은 수정하지 않았다.

forward-headers-strategy=framework를 기본으로 하므로 후속 ingress에서는 전달 header 신뢰 경계를 검증한다. Backend 직접 외부 노출을 허용하지 않는다.

## DB / Redis / Storage / 자원 설계

**신규 DB 버전은 PostgreSQL 17.11로 확정했다.** 사용자 후속 지시로 과거 DB 버전 추적은 종료했다. 17 계열은 2029-11-08까지 지원되고 2026-09-27 공식 최신 minor는17.11이다. 현재 Flyway11.7.2 소스의 권장 최신 major 기준도17이어서 18을 도입하며 dependency까지 바꾸지 않는다. image는 `postgres:17.11-bookworm@sha256:91eb910c44c7ed13f7f1a4ccadaa9ca72ef14cddc04cacb6e070e48eb44731a3` (amd64)로 고정했다. K3s에는 만들지 않고 임시 Docker DB에서 검증한다.

근거: [PostgreSQL versioning](https://www.postgresql.org/support/versioning/), [Flyway11.7.2 PostgreSQLDatabase](https://github.com/flyway/flyway/blob/flyway-11.7.2/flyway-database/flyway-database-postgresql/src/main/java/org/flywaydb/database/postgresql/PostgreSQLDatabase.java).

Flyway enabled=true, clean-disabled=true, 기본 migration 위치, Hibernate ddl-auto=validate로 고정했다. datasource→Flyway V1~V7→JPA 초기화 순서를 사용한다. migration SQL/entity는 수정하지 않았다. 테이블 이름/컬럼의 정적 대조는 실제 PostgreSQL 실행 및 Hibernate validation을 대신하지 않는다. 실제 17.11 빈 DB에서 V1~V7 및 ddl-auto=validate 통합 테스트를 수행하며 최종 결과를 아래 기록한다. 기존 데이터 이관은 history/checksum/baseline과 사전 백업을 별도 검토한다.

Redis는 인증 코드/검증 token/cooldown을 저장한다. 재시작 후 AOF가 정상 복구되고 TTL이 남아 있으면 이어갈 수 있지만 유실되면 인증 재요청이 필요하고 cooldown도 초기화된다. 접근 실패는 해당 인증 기능 실패로 이어진다. AOF1Gi 전용 PVC/StatefulSet1개/N100, exact Redis image pin은 배포 단계에서 검증한다. 단순 disposable cache로 취급하지 않는다.

PostgreSQL은 N100/StatefulSet1개/PVC10Gi, StorageClass n100-local-retain. 두 PVC는 별개이며 Retain/Prune 보호가 필요하다. 요청량은 quota가 아니며 같은 N100 300GiB filesystem을 공유한다. 아직 어떤 PVC도 생성하지 않았다.

Backend 첫 배포 N100/1replica, requests250m CPU/512Mi RAM, memory limit1Gi. CPU limit은 초기 미설정 권장(시작/GC throttling을 피하고 metrics로 관찰). heap512Mi 후보로 native/metaspace/thread 여유를 둔다. 실제 resource reservation/peak와 Grafana OOM/restart/CPU 추세를 확인해 조정한다. 향후 Pi 허용은 새 arm64 image 검증이 필요하며 이번 CI에 multi-arch를 추가하지 않았다.

## 검증 결과와 남은 gate

실제 실행 결과는 아래 최종 검증 절을 따른다. 새 health 테스트는 dependency 장애를 stub으로 재현하며 실제 PostgreSQL/Redis durability나 OAuth를 검증한 것은 아니다. 기존 contextLoads를 제거/skip하거나 -x test로 실패를 숨기지 않는다.

사용자 확인: 아직 미배포인 frontend의 향후 origin/로그인 성공·실패 redirect/허용 host, Google callback 등록, 후속 runtime credential 안전 입력, 초기 관리자/기준 데이터, 외부 백업. DB는 신규 생성으로 확정됐으며 과거 버전이나 이관 여부를 다시 묻지 않는다. Secret 값은 채팅에 보내지 않고 후속 SOPS 입력 단계에서만 제공한다.

commit/push 전에 변경 파일/test/Docker 결과를 사용자에게 보고하고 멈춘다. 현 workflow는 자동 EC2/old GitOps 배포를 유발할 수 있으므로 무조건 push하지 않는다. rollback은 이번 로컬 변경을 되돌리는 것으로 충분하다. Kubernetes 변경은 없다.

## PostgreSQL/Redis가 없는 테스트와 integration test 구분

- `./gradlew test`: 외부 PostgreSQL/Redis 없이 health indicator 장애, Security 접근 범위, CORS, runtime placeholder를 검사한다.
- `./gradlew integrationTest`: 전체 애플리케이션 context를 실제 임시 PostgreSQL/Redis에 연결한다. 기존 contextLoads를 이 태그로 분류하고 실제 fixture를 제공했다. 테스트를 삭제/무조건 skip한 것이 아니다.
- `python3 scripts/test-kubernetes.py`: 고정된 Docker image로 비공개 테스트 network, PostgreSQL tmpfs, 임시 Redis AOF 디렉터리를 만들고 위 두 task 및 kubernetesBootJar를 실행한다. host port 공개 없음. password는 매번 메모리에서 임의 생성해 runtime env/protected 설정 파일에만 전달한다. 외부 SMTP/OAuth/S3 호출은 하지 않는다. 종료 시 자기 테스트 container/network/data만 정리한다. 실행자에게 Docker 권한이 필요하며 host Java21은 없어도 된다.
- 실제 production credential을 넣지 않는다. 이 script는 DB/Flyway/schema 및 local runtime 검증이지 Google 계정 로그인이나 실제 email/S3 동작 검증은 아니다.
- 전체 검증이 통과하기 전 CI에 test 성공을 가정한 자동 push를 연결하지 않는다. 기존 CI가 -x test인 이유는 repository 이력에 명시돼 있지 않다. baseline에서 실제 DB 설정 부재 실패가 관측됐지만 이것이 과거 작성자의 의도였다고 단정하지 않는다.

## Google callback 검증 기준

registrationId는 `redot-member-google`, flow는 `redot-member`이다. 후속 Google Cloud Console의 Authorized redirect URIs에 추가할 값은:

`https://redot-api.dhxxn.kr/api/v1/sign-in/redot-member/social/callback/redot-member-google`

홈랩 `GOOGLE_REDIRECT_URI`에도 같은 값을 넣는다. 이는 frontend URL이 아니라 Backend OAuth callback이다. Security social chain matcher와 OAuth2LoginAuthenticationFilter가 이 경로를 처리하며 resolver 검증 테스트를 추가했다. Google credential 값은 지금 요청하지 않는다.

**기존 동작 한계:** 로그인 요청에 허용된 frontend `redirect_uri`를 보내면 FlowAwareRedirectUriResolver가 callback host도 그 frontend로 바꾼다. 이번에는 인증 구조 변경 금지에 따라 수정하지 않았다. Backend callback을 유지하려면 우선 override 없는 흐름을 검증하고, frontend 확정 후 frontend proxy 또는 승인된 resolver 수정 여부를 결정해야 한다. 성공/실패 redirect와 CORS는 runtime 변수이며 redot.dhxxn.kr 등을 실제 origin으로 확정하지 않았다.

## 최종 검증 — 2026-09-27 UTC

- 외부 DB 없는 test: 12개 성공. HealthProbe7 / CORS3 / runtime profile1 / legacy health opt-in1.
- 실제 PostgreSQL17.11 + Redis7.4.11 integrationTest: 5개 성공. 원래 contextLoads 유지, Flyway V1~V7 이력 확인과 Hibernate validate 완료, Redis TTL 저장, Google callback/security chain 일치, 기존 frontend override 동작, 실제 context probe 및 member API 비인증 차단 검증.
- Redis AOF 테스트 전용 비민감 marker를 쓰고 해당 테스트 container만 재시작하여 보존을 확인했다. 테스트 container/network/data는 모두 제거했다. K3s 리소스 생성 없음.
- 실제 JAR dependency: PostgreSQL JDBC42.7.7 / Flyway11.7.2 / Hibernate6.6.22.Final. SQL/entity 수정 없음.
- 합성 표식을 application-prod.yml/application-secret.yml/.env/secrets.txt에 넣은 packaging 음성 테스트 통과. JAR에는 공개 profile2개와 migration7개만 있고 표식은 없었다. 시험 파일은 제거했다. 실제 credential을 검사에 사용하지 않았다.
- Docker build 성공: local tag redot-server:k8s-preparation, linux/amd64, USER10001:10001, Java21.0.12.1, image size194143982bytes. 숫자 UID/GID를 사용하며 passwd의 별도 이름은 없다. `id -u/-g` 각각10001, read-only rootfs+tmpfs/tmp에서 Java 실행 검증.
- image 안 JAR와 검증한 artifact checksum 일치. 같은 고정 JDK/소스로 task를 재실행한 JAR SHA256도 일치했다. 새로운 source/dependency 버전까지 동일하다는 의미는 아니다.
- Dockerfile/Compose/기존 두 workflow/기존 SecurityConfig는 원본 byte 일치. 새 Actuator dependency와 public base settings가 추가되므로 실제 EC2 재배포 무영향까지 보장하지 않으며 현재 EC2에는 아무 변경도 하지 않았다.
- 단일 local image를 build했을 뿐 registry push/전체 image의 운영 credential 기반 기동/실제 Google 로그인·SMTP·S3 호출은 하지 않았다.

### BUILD FAILED 구분

| 단계 | 원인 | 처리/최종 상태 |
|---|---|---|
| 변경 전 main의 원래 test | profile/datasource URL 부재로 contextLoads 초기화 실패 | baseline failure. DB driver가 dependency에 없다는 뜻이 아님 |
| 새 테스트 첫 compile | CORS factory가 package-private여서 다른 package 테스트가 호출 못함 | 제가 작성한 오류. public으로 수정, compile 성공 |
| 새 health 테스트 초기 context | TestConfiguration이 main scan을 보완해 원치 않는 application bean을 로드 | 제가 작성한 fixture 오류. 별도 package/config로 격리하여 성공 |
| custom kubernetesBootJar | targetJavaVersion이 자동 채워지지 않음 | 제가 작성한 task 오류. Java21 명시 후 package 성공 |
| fixture 보완 전 전체 test | 기존 contextLoads의 datasource 실패 반복 | 원래 테스트를 integration으로 분류하고 임시 의존성 제공. 최종5개 통합 테스트 모두 성공 |

실패를 숨기는 -x test는 사용하지 않았다. baseline/개발 중 오류와 최종 결과는 구분한다. 민감할 수 있는 raw log 대신 docs/validation-results.json의 상태/파일 경로/버전/비민감 hash만 review 자료로 사용한다.

홈랩 전용 이미지 발행 준비: [HOMELAB-IMAGE-PUBLISHING.md](HOMELAB-IMAGE-PUBLISHING.md). 아직 commit/push 및 배포하지 않았습니다.
