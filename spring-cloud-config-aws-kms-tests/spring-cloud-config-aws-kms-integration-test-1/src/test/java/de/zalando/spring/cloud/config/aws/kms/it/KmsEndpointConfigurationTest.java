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
    private AWSKMS kms;

    @Test
    public void testPropertyHasBeenDecrypted() {
        assertThat(secret).isEqualTo("secret");
    }

    @Test
    public void testContext() {
        assertThat(kms)
                .isNotNull()
                .isInstanceOf(AWSKMSClient.class);

        AWSKMSClient client = (AWSKMSClient) kms;

        // prove aws.kms.endpoint.service-endpoint was used to configure the kms client
        Field endpointField = ReflectionUtils.findField(AWSKMSClient.class, "endpoint");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(endpointField));
        Object endpointObject = ReflectionUtils.getField(endpointField, client);
        assertThat(endpointObject)
                .isNotNull()
                .isInstanceOf(URI.class);
        URI endpoint = (URI) endpointObject;
        assertThat(endpoint.toString()).contains("us-east-1");

        // prove override was issued via the aws.kms.endpoint.signing-region property
        Field signerRegionField = ReflectionUtils.findField(AWSKMSClient.class, "signerRegionOverride");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(signerRegionField));
        Object signerRegionObject = ReflectionUtils.getField(signerRegionField, client);
        assertThat(signerRegionObject)
                .isNotNull()
                .isInstanceOf(String.class);
        String signerRegion = (String) signerRegionObject;
        assertThat(signerRegion).isEqualTo("us-east-2");
    }
}
