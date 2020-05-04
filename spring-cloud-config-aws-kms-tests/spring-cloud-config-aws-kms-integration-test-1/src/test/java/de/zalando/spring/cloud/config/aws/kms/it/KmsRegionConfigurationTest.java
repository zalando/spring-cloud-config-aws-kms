package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * During this integration test, a real AWSKMSClient is created, but there are no encrypted properties, so the client is
 * never used.<br/>
 * See src/test/resources/*-noEncryption.yml files.
 */
@SpringBootTest
@ActiveProfiles("noEncryption")
public class KmsRegionConfigurationTest {

    @Value("${secret}")
    private String secret;

    @Autowired
    private AWSKMS kms;

    @Test
    public void testPropertyIsAvailable() {
        assertThat(secret).isEqualTo("secret");
    }

    @Test
    public void testContext() {
        assertThat(kms)
                .isNotNull()
                .isInstanceOf(AWSKMSClient.class);

        // endpoint configured based on aws.kms.region property
        AWSKMSClient client = (AWSKMSClient) kms;
        Field field = ReflectionUtils.findField(AWSKMSClient.class, "endpoint");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(field));
        Object endpointObject = ReflectionUtils.getField(field, client);
        assertThat(endpointObject)
                .isNotNull()
                .isInstanceOf(URI.class);
        URI endpoint = (URI) endpointObject;
        assertThat(endpoint.toString()).contains("eu-central-1");

        // no override should occur in this configuration
        Field signerRegionField = ReflectionUtils.findField(AWSKMSClient.class, "signerRegionOverride");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(signerRegionField));
        Object signerRegionObject = ReflectionUtils.getField(signerRegionField, client);
        assertThat(signerRegionObject).isNull();
    }
}
