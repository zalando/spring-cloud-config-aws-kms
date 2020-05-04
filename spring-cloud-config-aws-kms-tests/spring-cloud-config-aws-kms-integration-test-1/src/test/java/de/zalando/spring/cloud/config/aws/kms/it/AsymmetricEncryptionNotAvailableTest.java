package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.InvalidCiphertextException;
import com.amazonaws.services.kms.model.InvalidKeyUsageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;

import java.nio.ByteBuffer;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("asymmetric")
@ExtendWith(OutputCaptureExtension.class)
public class AsymmetricEncryptionNotAvailableTest {

    private static final String PLAINTEXT = "Hello";
    private static final String CIPHERTEXT = "b29wcw==";
    private static final String VERSION_HINT = "Asymmetric encryption 'RSAES_OAEP_SHA_1' has been configured," +
            "but the version of aws-java-sdk you are using is outdated and does not support it. " +
            "Please upgrade to a more recent version.";

    @Autowired
    private AWSKMS mockKms;

    @Autowired
    private TextEncryptor textEncryptor;

    @Test
    void testAsymmetricEncryptionIsNotAvailable(CapturedOutput output) {
        doThrow(InvalidKeyUsageException.class).when(mockKms).encrypt(any(EncryptRequest.class));

        try {
            // Asymmetric algorithm is not available, because an outdated AWS SDK is used. The textEncryptor will
            // print a warning and fall back to symmetric algorithm.
            // Trying to use an asymmetric key with the symmetric algorithm will lead to an exception.
            textEncryptor.encrypt(PLAINTEXT);
            failBecauseExceptionWasNotThrown(InvalidKeyUsageException.class);
        } catch (InvalidKeyUsageException ignored) {
            assertThat(output).contains(VERSION_HINT);
            final EncryptRequest expectedRequest = new EncryptRequest()
                    .withKeyId("an-asymmetric-key")
                    .withPlaintext(ByteBuffer.wrap(PLAINTEXT.getBytes()));
            verify(mockKms).encrypt(eq(expectedRequest));
        }
    }

    @Test
    void testAsymmetricDecryptionIsNotAvailable(CapturedOutput output) {
        doThrow(InvalidCiphertextException.class).when(mockKms).decrypt(any(DecryptRequest.class));

        try {
            // Asymmetric algorithm is not available, because an outdated AWS SDK is used. The textEncryptor will
            // print a warning and fall back to symmetric algorithm.
            // Trying to use an asymmetric key with the symmetric algorithm will lead to an exception.
            textEncryptor.decrypt(CIPHERTEXT);
            failBecauseExceptionWasNotThrown(InvalidCiphertextException.class);
        } catch (InvalidCiphertextException ignored) {
            assertThat(output).contains(VERSION_HINT);
            final DecryptRequest expectedRequest = new DecryptRequest()
                    .withCiphertextBlob(ByteBuffer.wrap(Base64.getDecoder().decode(CIPHERTEXT.getBytes())));
            verify(mockKms).decrypt(eq(expectedRequest));
        }
    }
}
