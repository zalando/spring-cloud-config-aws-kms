package org.zalando.awsspring.cloud.bootstrap.encrypt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import software.amazon.awssdk.services.kms.KmsClient;

@Configuration
@EnableConfigurationProperties({ KmsProperties.class })
@ConditionalOnProperty(prefix = "encrypt.kms", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KmsEncryptConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public TextEncryptor textEncryptor(KmsClient kmsClient, KmsProperties properties) {
		return new KmsTextEncryptor(kmsClient, properties.getKeyId(), properties.getEncryptionAlgorithm());
	}

}
