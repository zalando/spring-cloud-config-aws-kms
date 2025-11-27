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
@ConditionalOnClass({ KmsClient.class, KmsAsyncClient.class })
@EnableConfigurationProperties({ KmsProperties.class })
@AutoConfigureAfter({CredentialsProviderAutoConfiguration.class, RegionProviderAutoConfiguration.class})
@ConditionalOnProperty(name = "spring.cloud.aws.kms.enabled", havingValue = "true", matchIfMissing = true)
public class KmsAutoConfiguration {

	private KmsProperties properties;

	public KmsAutoConfiguration(KmsProperties properties) {
		this.properties = properties;
	}

	@ConditionalOnMissingBean
	@Bean
	public KmsClient kmsClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsClientCustomizer<KmsClientBuilder>> configurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsClientCustomizer> kmsClientCustomizers,
			ObjectProvider<AwsSyncClientCustomizer> awsSyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureSyncClient(KmsClient.builder(), this.properties,
				connectionDetails.getIfAvailable(),
				configurer.getIfAvailable(),
				kmsClientCustomizers.orderedStream(),
				awsSyncClientCustomizers.orderedStream())
			.build();
	}

	@ConditionalOnMissingBean
	@Bean
		public KmsAsyncClient kmsAsyncClient(AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsClientCustomizer<KmsAsyncClientBuilder>> configurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsAsyncClientCustomizer> kmsAsyncClientCustomizers,
			ObjectProvider<AwsAsyncClientCustomizer> awsAsyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureAsyncClient(KmsAsyncClient.builder(), this.properties,
				connectionDetails.getIfAvailable(),
				configurer.getIfAvailable(),
				kmsAsyncClientCustomizers.orderedStream(),
				awsAsyncClientCustomizers.orderedStream()).build();
	}
}
