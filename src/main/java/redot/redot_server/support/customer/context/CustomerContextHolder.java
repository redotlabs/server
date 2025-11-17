package redot.redot_server.support.customer.context;

import java.util.Objects;

public final class CustomerContextHolder {

    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    private CustomerContextHolder() {
    }

    public static void set(Long customerId) {
        CONTEXT.set(Objects.requireNonNull(customerId, "customerId must not be null"));
    }

    public static Long get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
