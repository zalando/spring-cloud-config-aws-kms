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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * During this integration test, a real AWSKMSClient is created, but there are no encrypted properties, so te client is
 * never used.<br/>
 * See src/test/resources/*-noEncryption.yml files.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@IntegrationTest
@ActiveProfiles("noEncryption")
public class NoKmsEncryptionIntegrationConfigurationTest {

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
