package com.zalando.awsspring.cloud.bootstrap.encrypt;

import java.util.Objects;

public class EncryptedTokenOptions {

	private String keyId;

	private String encryptionAlgorithm;

	private OutputMode output;

	public EncryptedTokenOptions(String keyId, String encryptionAlgorithm) {
		this(keyId, encryptionAlgorithm, OutputMode.PLAIN);
	}

	public EncryptedTokenOptions(String keyId, String encryptionAlgorithm, OutputMode output) {
		this.keyId = keyId;
		this.encryptionAlgorithm = encryptionAlgorithm;
		this.output = output == null ? OutputMode.PLAIN : output;
	}

	public String getKeyId() {
		return keyId;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public OutputMode getOutput() {
		return output;
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		EncryptedTokenOptions other = (EncryptedTokenOptions) obj;
		return Objects.equals(keyId, other.keyId) && Objects.equals(encryptionAlgorithm, other.encryptionAlgorithm)
				&& Objects.equals(output, other.output);
	}

}
