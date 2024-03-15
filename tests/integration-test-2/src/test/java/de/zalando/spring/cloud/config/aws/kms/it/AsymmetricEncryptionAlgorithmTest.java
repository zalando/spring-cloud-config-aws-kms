package de.zalando.spring.cloud.config.aws.kms.it;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;


import static de.zalando.spring.cloud.config.aws.kms.MockAwsKmsConfig.PLAINTEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec.*;

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
    private KmsClient mockKms;

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

        DecryptRequest.Builder decryptRequest = DecryptRequest.builder();
        decryptRequest.ciphertextBlob(SdkBytes.fromByteBuffer(CIPHER_TEXT_BLOB1));
        decryptRequest.encryptionAlgorithm(RSAES_OAEP_SHA_1);
        decryptRequest.keyId("asymmetric-sha1-sample-key");
        verify(mockKms, atLeastOnce()).decrypt(any(DecryptRequest.class));
    }

    @Test
    void testAlgorithmsCanBeMixed() {
        assertThat(decryptedSecret2).isEqualTo(PLAINTEXT);

        DecryptRequest.Builder decryptRequest = DecryptRequest.builder();
        decryptRequest.ciphertextBlob(SdkBytes.fromByteBuffer(CIPHER_TEXT_BLOB2));
        decryptRequest.encryptionAlgorithm(SYMMETRIC_DEFAULT);
        decryptRequest.encryptionContext(Map.of());
        verify(mockKms, atLeastOnce()).decrypt(decryptRequest.build());
    }

    @Test
    void testSecretWithCustomKeyId() {
        assertThat(decryptedSecret3).isEqualTo(PLAINTEXT);

        DecryptRequest.Builder decryptRequest = DecryptRequest.builder();
        decryptRequest.ciphertextBlob(SdkBytes.fromByteBuffer(CIPHER_TEXT_BLOB3));
        decryptRequest.encryptionAlgorithm(RSAES_OAEP_SHA_256);
        decryptRequest.encryptionContext(Map.of());
        decryptRequest.keyId("different-key");
        verify(mockKms, atLeastOnce()).decrypt(decryptRequest.build());
    }

    @Test
    void testEncrypt() {
        final byte[] cipherTextBytes = "bla".getBytes();
        final String expectedCipherString = Base64.getEncoder().encodeToString(cipherTextBytes);
        doReturn(EncryptResponse.builder().ciphertextBlob(SdkBytes.fromByteArray(cipherTextBytes)).build())
                .when(mockKms).encrypt(any(EncryptRequest.class));

        final String mySecret = "my-secret";
        final String encryptedString = textEncryptor.encrypt(mySecret);
        assertThat(encryptedString).isEqualTo(expectedCipherString);

        final EncryptRequest encryptRequest = EncryptRequest.builder()
                .encryptionAlgorithm("RSAES_OAEP_SHA_1")
                .keyId("asymmetric-sha1-sample-key")
                .plaintext(SdkBytes.fromByteArray(mySecret.getBytes())).build();
        verify(mockKms).encrypt(eq(encryptRequest));
    }
}
