package de.zalando.spring.cloud.config.aws.kms.it;


import de.zalando.spring.cloud.config.aws.kms.MockAwsKmsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * This integration test shows the usage of spring-cloud-config-aws-kms. You will find an encrypted property within
 * src/test/resources/ that will be decrypted during the bootstrap phase. In order to make this test runnable on every
 * machine, a mock is used instead of a real AWSKMSClient.
 */
@SpringBootTest
@ActiveProfiles("encryption")
public class KmsEncryptionTest {

    private static final ByteBuffer CIPHER_TEXT_BLOB = ByteBuffer.wrap("secret".getBytes());

    @Autowired
    private KmsClient mockKms;

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasBeenDecrypted() {

        assertThat(decryptedSecret).isEqualTo(MockAwsKmsConfig.PLAINTEXT);

        final DecryptRequest decryptRequest = DecryptRequest.builder().ciphertextBlob(SdkBytes.fromByteBuffer(CIPHER_TEXT_BLOB)).build();
        verify(mockKms, atLeastOnce()).decrypt(any(DecryptRequest.class));
    }
}
