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

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import java.util.Base64;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;

public class KmsTextEncryptorTest {

    private static final String KMS_KEY_ID = "testKeyId";
    private static final String PLAINTEXT = "plaintext";
    private static final String CIPHER_TEXT = "C1PHERT3XT";
    private static final String BASE64_CIPHER_TEXT = Base64.getEncoder().encodeToString(CIPHER_TEXT.getBytes());

    private AWSKMS mockKms;
    private KmsTextEncryptor textEncryptor;
    private EncryptRequest expectedEncryptRequest;
    private EncryptResult encryptResult;
    private DecryptRequest expectedDecryptRequest;
    private DecryptResult decryptResult;

    @Before
    public void setUp() throws Exception {
        mockKms = mock(AWSKMS.class);
        textEncryptor = new KmsTextEncryptor(mockKms, KMS_KEY_ID);

        expectedEncryptRequest = new EncryptRequest();
        expectedEncryptRequest.setKeyId(KMS_KEY_ID);
        expectedEncryptRequest.setPlaintext(wrap(PLAINTEXT.getBytes()));

        encryptResult = new EncryptResult();
        encryptResult.setCiphertextBlob(wrap(CIPHER_TEXT.getBytes()));
        when(mockKms.encrypt(any(EncryptRequest.class))).thenReturn(encryptResult);

        expectedDecryptRequest = new DecryptRequest();
        expectedDecryptRequest.setCiphertextBlob(wrap(CIPHER_TEXT.getBytes()));

        decryptResult = new DecryptResult();
        decryptResult.setPlaintext(wrap(PLAINTEXT.getBytes()));
        when(mockKms.decrypt(any(DecryptRequest.class))).thenReturn(decryptResult);
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(mockKms);
    }

    @Test
    public void testEncrypt() throws Exception {
        assertThat(textEncryptor.encrypt(PLAINTEXT)).isEqualTo(BASE64_CIPHER_TEXT);
        verify(mockKms).encrypt(eq(expectedEncryptRequest));
    }

    @Test
    public void testEncryptEmptyResponse() throws Exception {
        encryptResult.setCiphertextBlob(allocate(0));
        assertThat(textEncryptor.encrypt(PLAINTEXT)).isEqualTo("");
        verify(mockKms).encrypt(eq(expectedEncryptRequest));
    }

    @Test
    public void testEncryptNull() throws Exception {
        assertThat(textEncryptor.encrypt(null)).isEqualTo("");
    }

    @Test
    public void testEncryptEmptyString() throws Exception {
        assertThat(textEncryptor.encrypt("")).isEqualTo("");
    }

    @Test
    public void testDecryptNull() throws Exception {
        assertThat(textEncryptor.decrypt(null)).isEqualTo("");
    }

    @Test
    public void testDecryptEmptyString() throws Exception {
        assertThat(textEncryptor.decrypt("")).isEqualTo("");
    }

    @Test
    public void testDecrypt() throws Exception {
        assertThat(textEncryptor.decrypt(BASE64_CIPHER_TEXT)).isEqualTo(PLAINTEXT);
        verify(mockKms).decrypt(eq(expectedDecryptRequest));
    }

    @Test
    public void testDecryptEmptyResult() throws Exception {
        decryptResult.setPlaintext(ByteBuffer.allocate(0));
        assertThat(textEncryptor.decrypt(BASE64_CIPHER_TEXT)).isEqualTo("");
        verify(mockKms).decrypt(eq(expectedDecryptRequest));
    }
}
