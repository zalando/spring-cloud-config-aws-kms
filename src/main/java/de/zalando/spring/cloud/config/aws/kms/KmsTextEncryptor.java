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

import java.nio.ByteBuffer;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import org.springframework.util.Assert;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;

/**
 * This {@link TextEncryptor} uses AWS KMS (Key Management Service) to encrypt / decrypt strings. Encoded cipher strings
 * are represented in Base64 format, to have a nicer string representation (only alpha-numeric chars), that can be
 * easily used as values in property files.
 */
public class KmsTextEncryptor implements TextEncryptor {

    private static final String EMPTY_STRING = "";

    private final AWSKMS kms;
    private final String kmsKeyId;

    /**
     * @param  kms       The AWS KMS client
     * @param  kmsKeyId  The ARN of the KMS key, e.g.
     *                   arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031. Must not be blank,
     *                   if you you want to encrypt text.
     */
    public KmsTextEncryptor(final AWSKMS kms, final String kmsKeyId) {
        Assert.notNull(kms, "KMS client must not be null");
        this.kms = kms;
        this.kmsKeyId = kmsKeyId;
    }

    @Override
    public String encrypt(final String text) {
        Assert.hasText(kmsKeyId, "kmsKeyId must not be blank");
        if (text == null || text.isEmpty()) {
            return EMPTY_STRING;
        } else {
            final EncryptRequest encryptRequest =
                new EncryptRequest().withKeyId(kmsKeyId) //
                                    .withPlaintext(ByteBuffer.wrap(text.getBytes()));

            final ByteBuffer encryptedBytes = kms.encrypt(encryptRequest).getCiphertextBlob();

            return extractString(ByteBuffer.wrap(Base64.encode(encryptedBytes.array())));
        }
    }

    @Override
    public String decrypt(final String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return EMPTY_STRING;
        } else {

            // Assuming the encryptedText is encoded in Base64
            final ByteBuffer encryptedBytes = ByteBuffer.wrap(Base64.decode(encryptedText.getBytes()));

            final DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(encryptedBytes);

            return extractString(kms.decrypt(decryptRequest).getPlaintext());
        }
    }

    private static String extractString(final ByteBuffer bb) {
        if (bb.hasRemaining()) {
            final byte[] bytes = new byte[bb.remaining()];
            bb.get(bytes, bb.arrayOffset(), bb.remaining());
            return new String(bytes);
        } else {
            return EMPTY_STRING;
        }
    }
}
