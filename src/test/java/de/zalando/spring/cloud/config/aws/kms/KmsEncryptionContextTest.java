package de.zalando.spring.cloud.config.aws.kms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for encryption context methods in {@link KmsTextEncryptor}.
 */
public class KmsEncryptionContextTest {

    private static final String ENCRYPTION_TEXT = "(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,"
            + "test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)remaining";

    @Test
    public void testExtractEncryptionContext() {
        assertThat(KmsTextEncryptor.extractEncryptionContext(ENCRYPTION_TEXT))
                .containsOnly(
                    entry("param", "L’homme c’est rien"),
                    entry("test", "l’oeuvre c’est tout"),
                    entry("valueless", ""));
    }

    @Test
    public void testExtractEncryptionValue() {
        String encryptionValue = KmsTextEncryptor.extractEncryptedValue(ENCRYPTION_TEXT);
        assertEquals("remaining", encryptionValue);
    }
}
