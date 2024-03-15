package com.zalando.awsspring.cloud.bootstrap.encrypt;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

/**
 * Implementation of TextEncryptor that uses AWS KMS. 
 */
public class KmsTextEncryptor implements TextEncryptor {
	
	private static Logger LOG = LoggerFactory.getLogger(KmsTextEncryptor.class);

	private final KmsClient kmsClient;

	private final String kmsKeyId;

	private final String kmsEncryptionAlgorithm;

	public KmsTextEncryptor(KmsClient kmsClient, String kmsKeyId, String kmsEncryptionAlgorithm) {
		this.kmsClient = kmsClient;
		this.kmsKeyId = kmsKeyId;
		this.kmsEncryptionAlgorithm = kmsEncryptionAlgorithm;
	}

	private String convertToString(byte[] cipherBytes, OutputMode output) {
		if (OutputMode.BASE64 == output) {
			return Base64.getEncoder().encodeToString(cipherBytes);
		} else {
			return new String(cipherBytes);
		}
	}

	
	@Override
	public String encrypt(String text) {
		EncryptRequest request = buildEncryptRequest(text);
		EncryptResponse response = kmsClient.encrypt(request);
		
		byte[] cipherBytes = response.ciphertextBlob().asByteArray();
		
		return convertToString(cipherBytes, OutputMode.BASE64);
	}

	private EncryptRequest buildEncryptRequest(String text) {
		EncryptRequest.Builder requestBuilder = EncryptRequest.builder().keyId(kmsKeyId)
				.plaintext(SdkBytes.fromUtf8String(text));
		
		if (kmsEncryptionAlgorithm != null) {
			requestBuilder = requestBuilder.encryptionAlgorithm(kmsEncryptionAlgorithm);
		}
		
		return requestBuilder.build();
	}
	
	
	@Override
	public String decrypt(String encryptedText) {
		
		EncryptedToken encryptedToken = EncryptedToken.parse(encryptedText);
		LOG.info("decrypting {} as part of stack.\n{}", encryptedText, Thread.currentThread().getStackTrace());
		
		DecryptRequest request = buildDecryptRequest(encryptedToken.getCipher());
		
		DecryptResponse response = kmsClient.decrypt(request);
		byte[] textBytes = response.plaintext().asByteArray();
		
		return convertToString(textBytes, OutputMode.PLAIN);
	}
	
	private DecryptRequest buildDecryptRequest(byte[] encryptedText) {
		DecryptRequest.Builder requestBuilder = DecryptRequest.builder();
		
		requestBuilder = requestBuilder.ciphertextBlob(SdkBytes.fromByteArray(encryptedText));
		if (kmsKeyId != null) {
			requestBuilder = requestBuilder.keyId(kmsKeyId);
		}
		if (kmsEncryptionAlgorithm != null) {
			requestBuilder = requestBuilder.encryptionAlgorithm(kmsEncryptionAlgorithm);
		}
		
		return requestBuilder.build();
		
	}
}
