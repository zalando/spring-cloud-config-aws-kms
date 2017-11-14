package de.zalando.spring.cloud.config.aws.kms;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * During this integration test, a real AWSKMSClient is created, but there are no encrypted properties, so te client is
 * never used.<br/>
 * See src/test/resources/*-noEncryption.yml files.
 */
@SpringBootTest
@ActiveProfiles("noEncryption")
public class NoKmsEncryptionIntegrationConfigurationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Value("${secret}")
    private String secret;

    @Test
    public void testPropertyHasBeenDecrypted() throws Exception {
        assertThat(secret).isEqualTo("secret");
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig { }
}
