package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * This integration test shows the usage of spring-cloud-config-aws-kms. You will find an encrypted property within
 * src/test/resources/ that will be decrypted during the bootstrap phase. In order to make this test runnable on every
 * machine, a mock is used instead of a real AWSKMSClient.
 */
@SpringBootTest
@ActiveProfiles("encryption")
public class KmsEncryptionIntegrationConfigurationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    static final String PLAINTEXT = "Hello World";
    private static final ByteBuffer CIPHER_TEXT_BLOB = ByteBuffer.wrap("secret".getBytes());

    @Autowired
    private AWSKMS mockKms;

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasBeenDecrypted() throws Exception {

        assertThat(decryptedSecret).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setCiphertextBlob(CIPHER_TEXT_BLOB);
        verify(mockKms, atLeastOnce()).decrypt(decryptRequest);
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
