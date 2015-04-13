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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;

/**
 * This config must be applied to the bootstrap context, which is done by META-INF/spring.factories.<br/>
 * The properties here can be configured in bootstrap.[yml|xml|properties], but not in application.[yml]xml|properties]
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "enabled", havingValue = "true", matchIfMissing = true)
class KmsEncryptionConfiguration {

    @Autowired
    private KmsTextEncryptor kmsTextEncryptor;

    @Bean
    EnvironmentDecryptApplicationInitializer environmentDecryptApplicationInitializer() {
        return new EnvironmentDecryptApplicationInitializer(kmsTextEncryptor);
    }

    @Configuration
    static class KmsTextEncryptorConfiguration {

        /**
         * The ARN of the KMS key, e.g. arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031
         */
        @Value("${aws.kms.keyId:}")
        private String kmsKeyId;

        @Autowired
        private AWSKMS kms;

        @Bean
        KmsTextEncryptor kmsTextEncryptor() {
            return new KmsTextEncryptor(kms, kmsKeyId);
        }
    }

    @Configuration
    @ConditionalOnMissingBean(AWSKMS.class)
    static class KmsConfiguration {

        @Value("#{T(com.amazonaws.regions.Regions).fromName('${aws.region}')}")
        private Regions region;

        @Bean
        AWSKMS kms() {
            final AWSKMSClient awskmsClient = new AWSKMSClient();
            awskmsClient.setRegion(region);
            return awskmsClient;
        }

    }
}
