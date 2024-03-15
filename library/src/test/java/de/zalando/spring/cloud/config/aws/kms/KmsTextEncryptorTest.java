package de.zalando.spring.cloud.config.aws.kms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

import java.util.Base64;

import static java.nio.ByteBuffer.wrap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT;

public class KmsTextEncryptorTest {

    private static final String KMS_KEY_ID = "testKeyId";
    private static final String PLAINTEXT = "plaintext";
    private static final String CIPHER_TEXT = "C1PHERT3XT";
    private static final String BASE64_CIPHER_TEXT =
            new String(Base64.getEncoder().encode(CIPHER_TEXT.getBytes()));
    private KmsClient mockKms;
    private KmsTextEncryptor textEncryptor;
    private EncryptRequest expectedEncryptRequest;
    private DecryptRequest expectedDecryptRequest;

    private EncryptResponse expectedEncryptResponse;
    private DecryptResponse expectedDecryptResponse;

    @BeforeEach
    public void setUp() {
        mockKms = mock(KmsClient.class);
        textEncryptor = new KmsTextEncryptor(mockKms, KMS_KEY_ID, SYMMETRIC_DEFAULT.toString());
        EncryptRequest.Builder requestBuilder =
                EncryptRequest.builder()
                        .keyId(KMS_KEY_ID)
                        .plaintext(SdkBytes.fromByteArray(PLAINTEXT.getBytes()))
                        .encryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        EncryptResponse.Builder encryptResponse =
                EncryptResponse.builder()
                        .keyId(KMS_KEY_ID)
                        .ciphertextBlob(SdkBytes.fromByteBuffer(wrap(CIPHER_TEXT.getBytes())))
                        .encryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        DecryptRequest.Builder decryptRequest =
                DecryptRequest.builder()
                        .ciphertextBlob(SdkBytes.fromByteBuffer(wrap(BASE64_CIPHER_TEXT.getBytes())))
                        .encryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        DecryptResponse.Builder decryptResponse =
                DecryptResponse.builder()
                        .keyId(KMS_KEY_ID)
                        .plaintext(SdkBytes.fromByteArray(PLAINTEXT.getBytes()))
                        .encryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        expectedEncryptRequest = requestBuilder.build();
        expectedEncryptResponse = encryptResponse.build();

        when(mockKms.encrypt(any(EncryptRequest.class))).thenReturn(encryptResponse.build());

        expectedDecryptRequest = decryptRequest.build();

        when(mockKms.decrypt(any(DecryptRequest.class))).thenReturn(decryptResponse.build());
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(mockKms);
    }

    @Test
    public void testEncrypt() {
        assertThat(textEncryptor.encrypt(PLAINTEXT)).isEqualTo(BASE64_CIPHER_TEXT);
        verify(mockKms).encrypt(eq(expectedEncryptRequest));
    }

    @Test
    public void testEncryptNull() {
        assertThat(textEncryptor.encrypt(null)).isEqualTo("");
    }

    @Test
    public void testEncryptEmptyString() {
        assertThat(textEncryptor.encrypt("")).isEqualTo("");
    }

    @Test
    public void testDecryptNull() {
        assertThat(textEncryptor.decrypt(null)).isEqualTo("");
    }

    @Test
    public void testDecryptEmptyString() {
        assertThat(textEncryptor.decrypt("")).isEqualTo("");
    }

    @Test
    public void testDecrypt() {
        assertThat(textEncryptor.decrypt(BASE64_CIPHER_TEXT)).isEqualTo(PLAINTEXT);
        verify(mockKms).decrypt(any(DecryptRequest.class));
    }
}
