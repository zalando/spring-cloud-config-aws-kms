package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("missingKeyId")
public class AsymmetricEncryptionMissingKeyIdTest {

    @Autowired
    private AWSKMS mockKms;

    @Autowired
    private TextEncryptor textEncryptor;

    @Test
    void testDecryptFails() {
        final String someCipher = Base64.getEncoder().encodeToString("SOME_CIPHER".getBytes());
        try {
            textEncryptor.decrypt(someCipher);
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("kmsKeyId must not be blank. Asymmetric decryption requires the key to be known");
        }
        verify(mockKms, never()).decrypt(any(DecryptRequest.class));
    }

    @Test
    void testEncryptFails() {
        try {
            textEncryptor.encrypt("Hello");
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("kmsKeyId must not be blank");
        }

        verify(mockKms, never()).encrypt(any(EncryptRequest.class));
    }
}
