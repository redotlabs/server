package redot.redot_server.support.redotapp.context;

import java.util.Objects;

public final class RedotAppContextHolder {

    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    private RedotAppContextHolder() {
    }

    public static void set(Long redotAppId) {
        CONTEXT.set(Objects.requireNonNull(redotAppId, "redotAppId must not be null"));
    }

    public static Long get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
