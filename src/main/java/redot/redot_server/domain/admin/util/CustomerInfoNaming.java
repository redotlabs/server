package redot.redot_server.domain.admin.util;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomerInfoNaming {

    public static String genUuid() {
        return UUID.randomUUID().toString();
    }

    /** 짧은 토큰(8자) */
    public static String genToken8() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /** 스키마명: t_xxxxxxxx */
    public static String toSchemaName(String token8) {
        return "t_" + token8;
    }

    public static String toDomain(String token) {
        return token;
    }
}
