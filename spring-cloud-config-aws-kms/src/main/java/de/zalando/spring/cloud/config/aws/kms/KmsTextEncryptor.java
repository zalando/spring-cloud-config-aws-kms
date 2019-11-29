package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Optional;

/**
 * This {@link TextEncryptor} uses AWS KMS (Key Management Service) to encrypt / decrypt strings. Encoded cipher strings
 * are represented in Base64 format, to have a nicer string representation (only alpha-numeric chars), that can be
 * easily used as values in property files.
 */
public class KmsTextEncryptor implements TextEncryptor {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final String EMPTY_STRING = "";

    private static final boolean IS_ALGORITHM_AVAILABLE;

    static {
        boolean available;
        try {
            Class.forName("com.amazonaws.services.kms.model.EncryptionAlgorithmSpec");
            available = true;
        } catch (Exception e) {
            available = false;
        }
        IS_ALGORITHM_AVAILABLE = available;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AWSKMS kms;
    private final String kmsKeyId;
    private final String encryptionAlgorithm;

    /**
     * @param kms                 The AWS KMS client
     * @param kmsKeyId            The ID or full ARN of the KMS key, e.g.
     *                            arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031. Must not be blank,
     * @param encryptionAlgorithm the encryption algorithm that should be used
     */
    public KmsTextEncryptor(final AWSKMS kms, final String kmsKeyId, final String encryptionAlgorithm) {
        Assert.notNull(kms, "KMS client must not be null");
        Assert.notNull(encryptionAlgorithm, "encryptionAlgorithm must not be null");
        this.kms = kms;
        this.kmsKeyId = kmsKeyId;
        this.encryptionAlgorithm = encryptionAlgorithm;

        checkAlgorithm(encryptionAlgorithm);
    }

    @Override
    public String encrypt(final String text) {
        Assert.hasText(kmsKeyId, "kmsKeyId must not be blank");
        if (text == null || text.isEmpty()) {
            return EMPTY_STRING;
        } else {
            final EncryptRequest encryptRequest = new EncryptRequest()
                    .withKeyId(kmsKeyId)
                    .withPlaintext(ByteBuffer.wrap(text.getBytes()));

            if (IS_ALGORITHM_AVAILABLE) {
                encryptRequest.setEncryptionAlgorithm(encryptionAlgorithm);
            }

            final ByteBuffer encryptedBytes = kms.encrypt(encryptRequest).getCiphertextBlob();

            return extractString(encryptedBytes, new KmsTextEncryptorOptions(OutputMode.BASE64, kmsKeyId, encryptionAlgorithm));
        }
    }

    @Override
    public String decrypt(final String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return EMPTY_STRING;
        } else {

            final EncryptedToken token = EncryptedToken.parse(encryptedText);

            final DecryptRequest decryptRequest = new DecryptRequest()
                    .withCiphertextBlob(token.getCipherBytes())
                    .withEncryptionContext(token.getEncryptionContext());
            final KmsTextEncryptorOptions options = token.getOptions();
            final String keyId = Optional.ofNullable(options.getKeyId()).orElse(kmsKeyId);
            final String algorithm = Optional.ofNullable(options.getEncryptionAlgorithm()).orElse(encryptionAlgorithm);

            checkAlgorithm(algorithm);

            if (IS_ALGORITHM_AVAILABLE) {
                decryptRequest.setEncryptionAlgorithm(algorithm);
                if (isAsymmetricEncryption(algorithm)) {
                    Assert.hasText(keyId, "kmsKeyId must not be blank. Asymmetric decryption requires the key to be known");
                    decryptRequest.setKeyId(keyId);
                }
            }

            return extractString(kms.decrypt(decryptRequest).getPlaintext(), options);
        }
    }

    private static String extractString(final ByteBuffer bb, final KmsTextEncryptorOptions options) {
        if (bb.hasRemaining()) {
            final byte[] bytes = new byte[bb.remaining()];
            bb.get(bytes, bb.arrayOffset(), bb.remaining());
            switch (options.getOutputMode()) {
                case BASE64:
                    return BASE64_ENCODER.encodeToString(bytes);
                default:
                    return new String(bytes);
            }
        } else {
            return EMPTY_STRING;
        }
    }

    private void checkAlgorithm(String algorithm) {
        if (isAsymmetricEncryption(algorithm) && !IS_ALGORITHM_AVAILABLE) {
            log.warn("Non-symmetric encryption '{}' has been configured," +
                    "but the version of aws-java-sdk you are using is outdated and does not support it. " +
                    "Please upgrade to a more recent version.", algorithm);
        }
    }

    private boolean isAsymmetricEncryption(String algorithm) {
        return !algorithm.equals("SYMMETRIC_DEFAULT");
    }
}
