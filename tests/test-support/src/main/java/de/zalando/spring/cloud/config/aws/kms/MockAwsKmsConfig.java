package de.zalando.spring.cloud.config.aws.kms;


import org.mockito.stubbing.Answer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "useMock", havingValue = "true")
@AutoConfigureBefore(KmsEncryptionConfiguration.class)
public class MockAwsKmsConfig {

    public static final String PLAINTEXT = "Hello World";

    private final Answer<?> defaultAnswer = invocation -> {
        final Method decryptMethod = KmsClient.class.getMethod("decrypt", DecryptRequest.class);
        if (invocation.getMethod().equals(decryptMethod)) {
            return DecryptResponse.builder().plaintext(SdkBytes.fromString(PLAINTEXT, StandardCharsets.UTF_8)).build();
        } else {
            throw new IllegalStateException("Unexpected mock invocation: " + invocation);
        }
    };

    @Bean
    KmsClient kms() {
        return mock(KmsClient.class, defaultAnswer);
    }
}
