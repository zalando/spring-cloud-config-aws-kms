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
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for encryption context methods in {@link KmsTextEncryptor}.
 */
public class KmsEncryptionContextTest {

    private static final String ENCRYPTION_TEXT = "(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,"
            + "test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)remaining";

    @Test
    public void testExtractEncryptionContext() {
        assertThat(KmsTextEncryptor.extractEncryptionContext(ENCRYPTION_TEXT))
                .containsOnly(
                    entry("param", "L’homme c’est rien"),
                    entry("test", "l’oeuvre c’est tout"),
                    entry("valueless", ""));
    }

    @Test
    public void testExtractEncryptionValue() {
        String encryptionValue = KmsTextEncryptor.extractEncryptedValue(ENCRYPTION_TEXT);
        assertEquals("remaining", encryptionValue);
    }
}
