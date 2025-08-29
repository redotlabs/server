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
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void createSchemaIfNotExists(String schemaName) {
        String quotedSchemaName = validateAndQuoteIdentifier(schemaName);
        jdbc.execute("CREATE SCHEMA IF NOT EXISTS " + quotedSchemaName);
    }

    /** 테넌트 스키마 내 customer 테이블 생성 */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void createTenantCustomerTableIfNotExists(String schemaName) {
        String quotedSchemaName = validateAndQuoteIdentifier(schemaName);

        jdbc.execute(
                "CREATE TABLE IF NOT EXISTS " + quotedSchemaName + ".customer (" +
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
                "CREATE UNIQUE INDEX IF NOT EXISTS one_row_customer ON " + quotedSchemaName + ".customer ((TRUE))"
        );
    }

    /** 한 줄 삽입 (UUID 문자열을 그대로 넣어도 JDBC가 uuid로 변환) */
    @SuppressWarnings("SqlSourceToSinkFlow")
    public void insertTenantCustomerRow(String schemaName, String customerId,
                                        String customerName, String theme) {
        String quotedSchemaName = validateAndQuoteIdentifier(schemaName);
        jdbc.update(
                "INSERT INTO " + quotedSchemaName + ".customer (id, name, theme) VALUES (?::uuid, ?, ?)",
                customerId, customerName, theme
        );
    }

    /**
     * 식별자가 안전한 문자(a-z, 0-9, _)로만 구성되어 있는지 검증하고,
     * 이상이 없다면 그대로 반환합니다.
     */
    private String validateAndQuoteIdentifier(String identifier) throws DataAccessException {
        if (identifier == null || !identifier.matches("[a-z0-9_]+")) {
            throw new IllegalArgumentException("Invalid schema/identifier: " + identifier);
        }
        return identifier;
    }
}
