package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import org.mockito.stubbing.Answer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.ByteBuffer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "useMock", havingValue = "true")
@AutoConfigureBefore(KmsEncryptionConfiguration.class)
public class MockAwsKmsConfig {

    public static final String PLAINTEXT = "Hello World";

    @Bean
    AWSKMS kms() {
        final AWSKMS mock = mock(AWSKMS.class);
        when(mock.decrypt(any(DecryptRequest.class))).thenAnswer((Answer<DecryptResult>) invocation ->
                new DecryptResult().withPlaintext(ByteBuffer.wrap(PLAINTEXT.getBytes())));
        return mock;
    }

}
