package com.zalando.awsspring.cloud.bootstrap.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(KmsProperties.PREFIX)
public class KmsProperties {
	
	public static final String PREFIX = "encrypt.kms";
	
	private String keyId;

	private String encryptionAlgorithm;

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String value) {
		this.keyId = value;
	}

	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(String value) {
		this.encryptionAlgorithm = value;
	}
}
