package redot.redot_server.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerDdlExecutor {

    private final JdbcTemplate jdbc;

    /** 스키마 생성 */
    public void createSchemaIfNotExists(String schemaName) {
        String safeSchemaName = toSafeIdentifier(schemaName);
        jdbc.execute("CREATE SCHEMA IF NOT EXISTS " + safeSchemaName);
    }

    /** 테넌트 스키마 내 customer 테이블 생성 */
    public void createTenantCustomerTableIfNotExists(String schemaName) {
        String safeSchemaName = toSafeIdentifier(schemaName);

        jdbc.execute(
                "CREATE TABLE IF NOT EXISTS " + safeSchemaName + ".customer (" +
                        "  id UUID PRIMARY KEY," +
                        "  name VARCHAR(255) NOT NULL," +
                        "  theme VARCHAR(32) NOT NULL," +
                        "  billing_info TEXT NULL," +
                        "  plan_options TEXT NULL," +
                        "  created_at TIMESTAMPTZ NOT NULL DEFAULT now()" +
                        ")"
        );

        // customer 테이블에 "최대 1행"만 허용하기 위한 고정식 유니크 인덱스 추가
        jdbc.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS one_row_customer ON " + safeSchemaName + ".customer ((TRUE))"
        );
    }

    /** 한 줄 삽입 (UUID 문자열을 그대로 넣어도 JDBC가 uuid로 변환) */
    public void insertTenantCustomerRow(String schemaName, String customerId,
                                        String customerName, String theme) {
        String safeSchemaName = toSafeIdentifier(schemaName);
        jdbc.update(
                "INSERT INTO " + safeSchemaName + ".customer (id, name, theme) VALUES (?::uuid, ?, ?)",
                customerId, customerName, theme
        );
    }


    /** 스키마/식별자 화이트리스트 (SQL 인젝션 방지) */
    private String toSafeIdentifier(String raw) throws DataAccessException {
        if (raw == null || !raw.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("Invalid schema/identifier: " + raw);
        }
        return raw;
    }
}
