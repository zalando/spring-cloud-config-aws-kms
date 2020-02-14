package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * During this integration test, a real AWSKMSClient is created, but there are no encrypted properties, so te client is
 * never used.<br/>
 * See src/test/resources/*-noEncryption.yml files.
 */
@SpringBootTest
@ActiveProfiles("noEncryptionEndpoint")
public class KmsEndpointConfigurationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Value("${secret}")
    private String secret;

    @Autowired
    private AWSKMS kms;

    @Test
    public void testPropertyHasBeenDecrypted() throws Exception {
        assertThat(secret).isEqualTo("secret");
    }

    @Test
    public void testContext() {
        assertNotNull(kms);
        assertTrue(kms instanceof AWSKMSClient);

        AWSKMSClient client = (AWSKMSClient) kms;

        // prove aws.kms.endpoint.service-endpoint was used to configure the kms client
        Field endpointField = ReflectionUtils.findField(AWSKMSClient.class, "endpoint");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(endpointField));
        Object endpointObject = ReflectionUtils.getField(endpointField, client);
        assertNotNull(endpointObject);
        assertTrue(endpointObject instanceof URI);
        URI endpoint = (URI) endpointObject;
        assertTrue(endpoint.toString().contains("us-east-1"));

        // prove override was issued via the aws.kms.endpoint.signing-region property
        Field signerRegionField = ReflectionUtils.findField(AWSKMSClient.class, "signerRegionOverride");
        ReflectionUtils.makeAccessible(Objects.requireNonNull(signerRegionField));
        Object signerRegionObject = ReflectionUtils.getField(signerRegionField, client);
        assertNotNull(signerRegionObject);
        assertTrue(signerRegionObject instanceof String);
        String signerRegion = (String) signerRegionObject;
        assertEquals("us-east-2", signerRegion);
    }
}
