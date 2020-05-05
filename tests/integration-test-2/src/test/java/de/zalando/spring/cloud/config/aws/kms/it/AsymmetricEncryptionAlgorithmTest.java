package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;

import java.nio.ByteBuffer;
import java.util.Base64;

import static com.amazonaws.services.kms.model.EncryptionAlgorithmSpec.RSAES_OAEP_SHA_1;
import static com.amazonaws.services.kms.model.EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256;
import static com.amazonaws.services.kms.model.EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT;
import static de.zalando.spring.cloud.config.aws.kms.MockAwsKmsConfig.PLAINTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("asymmetric")
public class AsymmetricEncryptionAlgorithmTest {

    private static final ByteBuffer CIPHER_TEXT_BLOB1 =
            ByteBuffer.wrap("I'm some asymmetrically encrypted secret".getBytes());
    private static final ByteBuffer CIPHER_TEXT_BLOB2 =
            ByteBuffer.wrap("Symmetric and asymmetric secrets can be mixed".getBytes());
    private static final ByteBuffer CIPHER_TEXT_BLOB3 =
            ByteBuffer.wrap("I have a custom key and algorithm".getBytes());

    @Autowired
    private AWSKMS mockKms;

    @Autowired
    private TextEncryptor textEncryptor;

    @Value("${secret1}")
    private String decryptedSecret1;

    @Value("${secret2}")
    private String decryptedSecret2;

    @Value("${secret3}")
    private String decryptedSecret3;

    @Test
    void testDecryptAsymmetricProperty() {
        assertThat(decryptedSecret1).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.withCiphertextBlob(CIPHER_TEXT_BLOB1);
        decryptRequest.withEncryptionAlgorithm(RSAES_OAEP_SHA_1);
        decryptRequest.withKeyId("asymmetric-sha1-sample-key");
        verify(mockKms, atLeastOnce()).decrypt(eq(decryptRequest));
    }

    @Test
    void testAlgorithmsCanBeMixed() {
        assertThat(decryptedSecret2).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.withCiphertextBlob(CIPHER_TEXT_BLOB2);
        decryptRequest.withEncryptionAlgorithm(SYMMETRIC_DEFAULT);
        verify(mockKms, atLeastOnce()).decrypt(eq(decryptRequest));
    }

    @Test
    void testSecretWithCustomKeyId() {
        assertThat(decryptedSecret3).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.withCiphertextBlob(CIPHER_TEXT_BLOB3);
        decryptRequest.withEncryptionAlgorithm(RSAES_OAEP_SHA_256);
        decryptRequest.withKeyId("different-key");
        verify(mockKms, atLeastOnce()).decrypt(eq(decryptRequest));
    }

    @Test
    void testEncrypt() {
        final byte[] cipherTextBytes = "bla".getBytes();
        final String expectedCipherString = Base64.getEncoder().encodeToString(cipherTextBytes);
        doReturn(new EncryptResult().withCiphertextBlob(ByteBuffer.wrap(cipherTextBytes)))
                .when(mockKms).encrypt(any(EncryptRequest.class));

        final String mySecret = "my-secret";
        final String encryptedString = textEncryptor.encrypt(mySecret);
        assertThat(encryptedString).isEqualTo(expectedCipherString);

        final EncryptRequest encryptRequest = new EncryptRequest()
                .withEncryptionAlgorithm("RSAES_OAEP_SHA_1")
                .withKeyId("asymmetric-sha1-sample-key")
                .withPlaintext(ByteBuffer.wrap(mySecret.getBytes()));
        verify(mockKms).encrypt(eq(encryptRequest));
    }
}
