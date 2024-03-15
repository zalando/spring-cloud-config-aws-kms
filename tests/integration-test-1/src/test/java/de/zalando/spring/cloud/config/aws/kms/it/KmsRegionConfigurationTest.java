package de.zalando.spring.cloud.config.aws.kms.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ReflectionUtils;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsServiceClientConfiguration;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

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
    private KmsClient kms;

    @Test
    public void testPropertyIsAvailable() {
        assertThat(secret).isEqualTo("secret");
    }

    @Test
    public void testContext() {
        assertThat(kms)
                .isNotNull()
                .isInstanceOf(AwsClient.class);

        // endpoint configured based on aws.kms.region property
        KmsServiceClientConfiguration kmsServiceClientConfiguration = kms.serviceClientConfiguration();
        Optional<URI> endpointObject = kmsServiceClientConfiguration.endpointOverride();
        assertThat(endpointObject)
                .isEmpty();
        // no override should occur in this configuration
        Region region = kmsServiceClientConfiguration.region();
        assertThat(region.toString()).contains("eu-central-1");
    }
}
