package de.zalando.spring.cloud.config.aws.kms.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This integration test shows the usage of "aws.kms.enabled=false" to completely disable KMS, although there are
 * encrypted properties. This may be useful for local development. Please note the "encrypt.failOnError=false" property.
 * If absent the application startup would fail fast, if any cipher-property cannot be decrypted.
 */
@SpringBootTest({"aws.kms.enabled=false", "encrypt.failOnError=false"})
@ActiveProfiles("encryption")
public class DisabledKmsEncryptionTest {

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasNotBeenDecrypted() {
        assertThat(decryptedSecret).isEmpty();
    }
}
