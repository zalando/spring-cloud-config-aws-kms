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

/**
 * This {@link TextEncryptor} uses AWS KMS (Key Management Service) to encrypt / decrypt strings. Encoded cipher strings
 * are represented in Base64 format, to have a nicer string representation (only alpha-numeric chars), that can be
 * easily used as values in property files.
 */
public class KmsTextEncryptor implements TextEncryptor {

    private static final String EMPTY_STRING = "";

    private final String kmsKeyId;
    private final Regions region;

    private AWSKMSClient kms;

    /**
     * @param  kmsKeyId  The ARN of the KMS key, e.g.
     *                   arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031. Must not be null
     * @param  region    The region of your KMS key, e.g. eu-west-1. Must not be null
     */
    public KmsTextEncryptor(final String kmsKeyId, final Regions region) {
        Assert.notNull(region, "region must not be null");
        Assert.hasText(kmsKeyId, "kmsKeyId must not be blank");
        this.kmsKeyId = kmsKeyId;
        this.region = region;
    }

    protected AWSKMSClient getKms() {
        if (kms == null) {
            kms = new AWSKMSClient();
            kms.setRegion(region);
        }

        return kms;
    }

    @Override
    public String encrypt(final String text) {
        return extractString(Base64.getEncoder().encode(
                    getKms().encrypt(new EncryptRequest().withKeyId(kmsKeyId).withPlaintext(
                            ByteBuffer.wrap(text.getBytes()))).getCiphertextBlob()));
    }

    @Override
    public String decrypt(final String encryptedText) {
        return extractString(getKms().decrypt(new DecryptRequest().withCiphertextBlob(
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
