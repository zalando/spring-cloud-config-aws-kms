package de.zalando.spring.cloud.config.aws.kms;

import java.util.Objects;

class KmsTextEncryptorOptions {

    private static final OutputMode DEFAULT_OUTPUT_MODE = OutputMode.PLAIN;

    private final OutputMode outputMode;

    private final String keyId;

    private final String encryptionAlgorithm;

    KmsTextEncryptorOptions(OutputMode outputMode, String keyId, String encryptionAlgorithm) {
        this.outputMode = outputMode == null ? DEFAULT_OUTPUT_MODE : outputMode;
        this.keyId = keyId;
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    OutputMode getOutputMode() {
        return outputMode;
    }

    String getKeyId() {
        return keyId;
    }

    String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KmsTextEncryptorOptions that = (KmsTextEncryptorOptions) o;
        return outputMode == that.outputMode &&
                Objects.equals(keyId, that.keyId) &&
                Objects.equals(encryptionAlgorithm, that.encryptionAlgorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputMode, keyId, encryptionAlgorithm);
    }
}
