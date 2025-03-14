package org.zalando.awsspring.cloud.autoconfigure.kms;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.awspring.cloud.autoconfigure.AwsAsyncClientCustomizer;
import io.awspring.cloud.autoconfigure.AwsSyncClientCustomizer;
import io.awspring.cloud.autoconfigure.core.AwsClientBuilderConfigurer;
import io.awspring.cloud.autoconfigure.core.AwsClientCustomizer;
import io.awspring.cloud.autoconfigure.core.AwsConnectionDetails;
import io.awspring.cloud.autoconfigure.core.CredentialsProviderAutoConfiguration;
import io.awspring.cloud.autoconfigure.core.RegionProviderAutoConfiguration;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.KmsAsyncClientBuilder;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

@AutoConfiguration
@EnableConfigurationProperties({ KmsProperties.class })
@ConditionalOnClass({ KmsClient.class, KmsAsyncClient.class })
@AutoConfigureAfter({CredentialsProviderAutoConfiguration.class, RegionProviderAutoConfiguration.class})
@ConditionalOnProperty(name = "spring.cloud.aws.kms.enabled", havingValue = "true", matchIfMissing = true)
public class KmsAutoConfiguration {

	private KmsProperties properties;

	public KmsAutoConfiguration(KmsProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public KmsClient kmsClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsClientCustomizer<KmsClientBuilder>> configurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsClientCustomizer> kmsClientCustomizer,
			ObjectProvider<AwsSyncClientCustomizer> awsSyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureSyncClient(KmsClient.builder(), this.properties,
				connectionDetails.getIfAvailable(),
				configurer.getIfAvailable(),
				kmsClientCustomizer.orderedStream(),
				awsSyncClientCustomizers.orderedStream())
			.build();
	}

	@Bean
	@ConditionalOnMissingBean
	public KmsAsyncClient kmsAsyncClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsClientCustomizer<KmsAsyncClientBuilder>> configurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsAsyncClientCustomizer> kmsAsyncClientCustomizer,
			ObjectProvider<AwsAsyncClientCustomizer> awsAsyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureAsyncClient(KmsAsyncClient.builder(), this.properties,
				connectionDetails.getIfAvailable(),
				configurer.getIfAvailable(),
				kmsAsyncClientCustomizer.orderedStream(),
				awsAsyncClientCustomizers.orderedStream()).build();
	}
}
