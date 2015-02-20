/*
 * Copyright 2015 Zalando SE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.zalando.spring.cloud.config.aws.kms;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.nio.ByteBuffer;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;

import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;

/**
 * This integration test shows the usage of spring-cloud-config-aws-kms. You will find an encrypted property within
 * src/test/resources/ that will be decrypted during the bootstrap phase. In order to make this test runnable on every
 * machine, a mock is used instead of a real AWSKMSClient.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@IntegrationTest
@ActiveProfiles("encryption")
public class KmsEncryptionIntegrationConfigurationTest {

    static final String PLAINTEXT = "Hello World";
    static final ByteBuffer CIPHER_TEXT_BLOB = ByteBuffer.wrap("secret".getBytes());

    @Autowired
    private AWSKMS mockKms;

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasBeenDecrypted() throws Exception {

        assertThat(decryptedSecret).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setCiphertextBlob(CIPHER_TEXT_BLOB);
        verify(mockKms, atLeastOnce()).decrypt(decryptRequest);
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig { }
}
