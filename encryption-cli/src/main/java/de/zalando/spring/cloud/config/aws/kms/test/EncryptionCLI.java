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

package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import org.springframework.util.Assert;

import com.amazonaws.regions.Regions;

import de.zalando.spring.cloud.config.aws.kms.KmsTextEncryptor;

@Component
public class EncryptionCLI implements CommandLineRunner {

    @Override
    public void run(final String... args) {
        Assert.state(args.length >= 3, "Too few arguments");

        final String text = args[1];
        final String keyId = args[2];

        switch (args[0]) {

            case "encrypt" :

                System.out.println(new KmsTextEncryptor(keyId, Regions.EU_WEST_1).encrypt(text));
                break;

            case "decrypt" :
                System.out.println(new KmsTextEncryptor(keyId, Regions.EU_WEST_1).decrypt(text));
                break;

            default :

                break;
        }
    }
}
