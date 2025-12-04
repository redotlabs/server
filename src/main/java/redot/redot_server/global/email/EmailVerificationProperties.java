package redot.redot_server.global.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "email.verification")
public class EmailVerificationProperties {

    private int codeLength = 6;
    private int codeTtlSeconds = 300;
    private int tokenTtlSeconds = 900;
    private int resendCooldownSeconds = 5;
    private String fromAddress = "no-reply@redot.me";
    private String brandName = "Redot";
}
