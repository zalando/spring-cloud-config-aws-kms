package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Base64;

import static com.amazonaws.services.kms.model.EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class KmsTextEncryptorTest {

    private static final String KMS_KEY_ID = "testKeyId";
    private static final String PLAINTEXT = "plaintext";
    private static final String CIPHER_TEXT = "C1PHERT3XT";
    private static final String BASE64_CIPHER_TEXT = new String(Base64.getEncoder().encode(CIPHER_TEXT.getBytes()));

    private AWSKMS mockKms;
    private KmsTextEncryptor textEncryptor;
    private EncryptRequest expectedEncryptRequest;
    private EncryptResult encryptResult;
    private DecryptRequest expectedDecryptRequest;
    private DecryptResult decryptResult;

    @BeforeEach
    public void setUp() {
        mockKms = mock(AWSKMS.class);
        textEncryptor = new KmsTextEncryptor(mockKms, KMS_KEY_ID, SYMMETRIC_DEFAULT.toString());

        expectedEncryptRequest = new EncryptRequest();
        expectedEncryptRequest.setKeyId(KMS_KEY_ID);
        expectedEncryptRequest.setPlaintext(wrap(PLAINTEXT.getBytes()));
        expectedEncryptRequest.setEncryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        encryptResult = new EncryptResult();
        encryptResult.setCiphertextBlob(wrap(CIPHER_TEXT.getBytes()));
        when(mockKms.encrypt(any(EncryptRequest.class))).thenReturn(encryptResult);

        expectedDecryptRequest = new DecryptRequest();
        expectedDecryptRequest.setCiphertextBlob(wrap(CIPHER_TEXT.getBytes()));
        expectedDecryptRequest.setEncryptionAlgorithm(SYMMETRIC_DEFAULT.toString());

        decryptResult = new DecryptResult();
        decryptResult.setPlaintext(wrap(PLAINTEXT.getBytes()));
        when(mockKms.decrypt(any(DecryptRequest.class))).thenReturn(decryptResult);
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
    public void testEncryptEmptyResponse() {
        encryptResult.setCiphertextBlob(allocate(0));
        assertThat(textEncryptor.encrypt(PLAINTEXT)).isEqualTo("");
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
        verify(mockKms).decrypt(eq(expectedDecryptRequest));
    }

    @Test
    public void testDecryptEmptyResult() {
        decryptResult.setPlaintext(ByteBuffer.allocate(0));
        assertThat(textEncryptor.decrypt(BASE64_CIPHER_TEXT)).isEqualTo("");
        verify(mockKms).decrypt(eq(expectedDecryptRequest));
    }
}
