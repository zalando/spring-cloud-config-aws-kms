package de.zalando.spring.cloud.config.aws.kms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.core.SdkBytes;

import java.util.Base64;
import java.util.Optional;




import static de.zalando.spring.cloud.config.aws.kms.OutputMode.BASE64;

/**
 * This {@link TextEncryptor} uses AWS KMS (Key Management Service) to encrypt / decrypt strings.
 * Encoded cipher strings are represented in Base64 format, to have a nicer string representation
 * (only alpha-numeric chars), that can be easily used as values in property files.
 */

public class KmsTextEncryptor implements TextEncryptor {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final String EMPTY_STRING = "";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final boolean IS_ALGORITHM_AVAILABLE;

    static {
        boolean available;
        try {
            Class.forName("software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec");
            available = true;
        } catch (Exception e) {
            available = false;
        }
        IS_ALGORITHM_AVAILABLE = available;
    }

    private final KmsClient kms;
    private final String kmsKeyId;
    private final String encryptionAlgorithm;

    /**
     * @param kms The AWS KMS client
     * @param kmsKeyId The ID or full ARN of the KMS key, e.g.
     *     arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031. Must not be
     *     blank,
     * @param encryptionAlgorithm the encryption algorithm that should be used
     */
    public KmsTextEncryptor(
            final KmsClient kms, final String kmsKeyId, final String encryptionAlgorithm) {
        Assert.notNull(kms, "KMS client must not be null");
        Assert.notNull(encryptionAlgorithm, "encryptionAlgorithm must not be null");
        this.kms = kms;
        this.kmsKeyId = kmsKeyId;
        this.encryptionAlgorithm = encryptionAlgorithm;

        checkAlgorithm(encryptionAlgorithm);
    }

    private String extractString(final SdkBytes bb, final OutputMode outputMode) {
        if (!bb.asByteBuffer().hasRemaining()) {
            return EMPTY_STRING;
        }

        byte[] bytes = bb.asByteArray();

        if (outputMode != BASE64) {
            return logValue(new String(bytes));
        }
        return logValue(BASE64_ENCODER.encodeToString(bytes));
    }

    private String logValue(String value) {
        log.debug("decrypt value is {}", value);
        return value;
    }

    private static boolean isAsymmetricEncryption(String algorithm) {
        return !algorithm.equals("SYMMETRIC_DEFAULT");
    }

    @Override
    public String encrypt(final String text) {
        Assert.hasText(kmsKeyId, "kmsKeyId must not be blank");
        if (text == null || text.isEmpty()) {
            return EMPTY_STRING;
        } else {
            EncryptRequest.Builder requestBuilder =
                    EncryptRequest.builder()
                            .keyId(kmsKeyId)
                            .plaintext(SdkBytes.fromByteArray(text.getBytes()));

            checkAlgorithm(encryptionAlgorithm);

            if (IS_ALGORITHM_AVAILABLE) {
                requestBuilder.encryptionAlgorithm(encryptionAlgorithm);
            }

            EncryptResponse encryptResponse = kms.encrypt(requestBuilder.build());

            return extractString(encryptResponse.ciphertextBlob(), BASE64);
        }
    }

    @Override
    public String decrypt(final String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return EMPTY_STRING;
        } else {

            final EncryptedToken token = EncryptedToken.parse(encryptedText);

            DecryptRequest.Builder decryptRequest =
                    DecryptRequest.builder()
                            .ciphertextBlob(SdkBytes.fromByteBuffer(token.getCipherBytes()))
                            .encryptionContext(token.getEncryptionContext());

            final KmsTextEncryptorOptions options = token.getOptions();
            final String keyId = Optional.ofNullable(options.getKeyId()).orElse(kmsKeyId);
            final String algorithm =
                    Optional.ofNullable(options.getEncryptionAlgorithm()).orElse(encryptionAlgorithm);

            checkAlgorithm(algorithm);

            if (IS_ALGORITHM_AVAILABLE) {
                decryptRequest.encryptionAlgorithm(algorithm);
                if (isAsymmetricEncryption(algorithm)) {
                    Assert.hasText(
                            keyId,
                            "kmsKeyId must not be blank. Asymmetric decryption requires the key to be known");
                    decryptRequest.keyId(keyId);
                }
            }

            DecryptResponse decryptResponse = kms.decrypt(decryptRequest.build());

            return extractString(decryptResponse.plaintext(), options.getOutputMode());
        }
    }

    private void checkAlgorithm(String algorithm) {
        if (isAsymmetricEncryption(algorithm) && !IS_ALGORITHM_AVAILABLE) {
            log.warn(
                    "Asymmetric encryption '{}' has been configured,but the version of aws-java-sdk you are"
                            + " using is outdated and does not support it. Please upgrade to a more recent"
                            + " version.",
                    algorithm);
        }
    }
}
