package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import org.mockito.stubbing.Answer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "useMock", havingValue = "true")
@AutoConfigureBefore(KmsEncryptionConfiguration.class)
public class MockAwsKmsConfig {

    public static final String PLAINTEXT = "Hello World";

    private final Answer<?> defaultAnswer = invocation -> {
        final Method decryptMethod = AWSKMS.class.getMethod("decrypt", DecryptRequest.class);
        if (invocation.getMethod().equals(decryptMethod)) {
            return new DecryptResult().withPlaintext(ByteBuffer.wrap(PLAINTEXT.getBytes()));
        } else {
            throw new IllegalStateException("Unexpected mock invocation: " + invocation);
        }
    };

    @Bean
    AWSKMS kms() {
        return mock(AWSKMS.class, defaultAnswer);
    }
}
