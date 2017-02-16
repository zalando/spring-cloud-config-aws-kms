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

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * Tests for encryption context methods in {@link KmsTextEncryptor}.
 */
public class KmsEncryptionContextTest {

    private static final String ENCRYPTION_TEXT = "(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,"
            + "test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)remaining";
    private static final int NUMBER_OF_ENTRIES = 3;

    @Test
    public void testExtractEncryptionContext() {
        Map<String, String> encryptionContext = KmsTextEncryptor.extractEncryptionContext(ENCRYPTION_TEXT);
        assertEquals(NUMBER_OF_ENTRIES, encryptionContext.size());
        int found = 0;
        for (Entry<String, String> entry : encryptionContext.entrySet()) {
            if ("param".equals(entry.getKey())) {
                assertEquals("L’homme c’est rien", entry.getValue());
                found++;
            } else if ("test".equals(entry.getKey())) {
                assertEquals("l’oeuvre c’est tout", entry.getValue());
                found++;
            } else if ("valueless".equals(entry.getKey())) {
                assertEquals("An empty value must still be added!", "", entry.getValue());
                found++;
            }
        }
        assertEquals("Test did not find right number of key and value pairs", NUMBER_OF_ENTRIES, found);
    }

    @Test
    public void testExtractEncryptionValue() {
        String encryptionValue = KmsTextEncryptor.extractEncryptedValue(ENCRYPTION_TEXT);
        assertEquals("remaining", encryptionValue);
    }
}
