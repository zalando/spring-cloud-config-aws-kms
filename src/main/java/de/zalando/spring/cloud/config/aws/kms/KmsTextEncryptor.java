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

import java.util.Base64;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import org.springframework.util.Assert;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;

public class KmsTextEncryptor implements TextEncryptor {

    private static final String EMPTY_STRING = "";

    private final String kmsKeyId;
    private final AWSKMSClient kms;

    public KmsTextEncryptor(final String kmsKeyId, final Regions region) {
        Assert.notNull(region, "region must not be null");
        Assert.hasText(kmsKeyId, "kmsKeyId must not be blank");
        this.kmsKeyId = kmsKeyId;

        this.kms = new AWSKMSClient();
        this.kms.setRegion(region);

    }

    @Override
    public String encrypt(final String text) {
        return extractString(Base64.getEncoder().encode(
                    kms.encrypt(new EncryptRequest().withKeyId(kmsKeyId).withPlaintext(
                            ByteBuffer.wrap(text.getBytes()))).getCiphertextBlob()));
    }

    @Override
    public String decrypt(final String encryptedText) {
        return extractString(kms.decrypt(new DecryptRequest().withCiphertextBlob(
                        Base64.getDecoder().decode(ByteBuffer.wrap(encryptedText.getBytes())))).getPlaintext());
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
