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

import org.springframework.beans.factory.annotation.Value;

import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.amazonaws.regions.Regions;

/**
 * This config must be applied to the bootstrap context, which is done by META-INF/spring.factories.<br/>
 * The properties here can be configured in bootstrap.[yml|xml|properties], but not in application.[yml]xml|properties]
 */
@Configuration
class KmsEncryptionConfiguration {

    /**
     * The ARN of the KMS key, e.g. arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031
     */
    @Value("${aws.kms.keyId}")
    private String kmsKeyId;

    /**
     * The region of your KMS key, e.g. eu-west-1
     */
    @Value("${aws.region}")
    private String regionName;

    @Bean
    TextEncryptor kmsTextEncryptor() {
        return new KmsTextEncryptor(kmsKeyId, Regions.fromName(regionName));
    }

    @Bean
    EnvironmentDecryptApplicationListener environmentDecryptApplicationListener() {
        return new EnvironmentDecryptApplicationListener(kmsTextEncryptor());
    }

}
