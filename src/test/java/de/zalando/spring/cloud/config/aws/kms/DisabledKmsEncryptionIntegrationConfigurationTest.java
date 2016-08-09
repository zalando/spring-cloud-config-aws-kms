/**
 * Copyright (C) 2015 Zalando SE (https://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * This integration test shows the usage of "aws.kms.enabled=false" to completely disable KMS, although there are
 * encrypted properties. This may be useful for local development. Please note the "encrypt.failOnError=false" property.
 * If absent the application startup would fail fast, if any cipher-property cannot be decrypted.
 */
@SpringBootTest({"aws.kms.enabled=false", "encrypt.failOnError=false"})
@ActiveProfiles("encryption")
public class DisabledKmsEncryptionIntegrationConfigurationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasNotBeenDecrypted() throws Exception {
        assertThat(decryptedSecret).isEmpty();
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig { }
}
