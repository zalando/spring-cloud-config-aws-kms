package de.zalando.spring.cloud.config.aws.kms.it;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ReflectionUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;
import software.amazon.awssdk.services.kms.KmsServiceClientConfiguration;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * During this integration test, a real AWSKMSClient is created, but there are no encrypted properties, so te client is
 * never used.<br/>
 * See src/test/resources/*-noEncryption.yml files.
 */
@SpringBootTest
@ActiveProfiles("noEncryptionEndpoint")
public class KmsEndpointConfigurationTest {


    @Value("${secret}")
    private String secret;

    @Autowired
    private KmsClient kms;

    @Test
    public void testPropertyHasBeenDecrypted() {
        assertThat(secret).isEqualTo("secret");
    }

    @Test
    public void testContext() {
        assertThat(kms)
                .isNotNull()
                .isInstanceOf(KmsClient.class);

        // prove aws.kms.endpoint.service-endpoint was used to configure the kms client

        KmsServiceClientConfiguration kmsServiceClientConfiguration = kms.serviceClientConfiguration();
        Optional<URI> endpointObject = kmsServiceClientConfiguration.endpointOverride();
        assertThat(endpointObject)
                .isNotNull()
                .isInstanceOf(Optional.class);
        URI endpoint = endpointObject.get();
        assertThat(endpoint.toString()).contains("us-east-1");

        // prove override was issued via the aws.kms.endpoint.signing-region property
        Region signerRegion = kmsServiceClientConfiguration.region();
        assertThat(signerRegion.id()).isEqualTo("us-west-1");
    }
}
