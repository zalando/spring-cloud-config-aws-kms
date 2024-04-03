package org.zalando.awsspring.cloud.autoconfigure.kms;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.awspring.cloud.autoconfigure.core.AwsClientBuilderConfigurer;
import io.awspring.cloud.autoconfigure.core.AwsClientCustomizer;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.KmsAsyncClientBuilder;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

@Configuration
@ConditionalOnClass({ KmsClient.class, KmsAsyncClient.class })
@EnableConfigurationProperties({ KmsProperties.class })
@ConditionalOnProperty(name = "spring.cloud.aws.kms.enabled", havingValue = "true", matchIfMissing = true)
public class KmsAutoConfiguration {

	public KmsAutoConfiguration() {

	}

	@ConditionalOnMissingBean
	@Bean
	public KmsClient kmsClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer, ObjectProvider<AwsClientCustomizer<KmsClientBuilder>> configurer, KmsProperties properties) {
		return awsClientBuilderConfigurer.configure(KmsClient.builder(), properties, configurer.getIfAvailable()).build();
	}

	@ConditionalOnMissingBean
	@Bean
	public KmsAsyncClient kmsAsyncClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer, ObjectProvider<AwsClientCustomizer<KmsAsyncClientBuilder>> configurer,
			KmsProperties properties) {
		return awsClientBuilderConfigurer.configure(KmsAsyncClient.builder(), properties, configurer.getIfAvailable()).build();
	}
}
