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
import io.awspring.cloud.autoconfigure.core.AwsConnectionDetails;
import io.awspring.cloud.autoconfigure.core.CredentialsProviderAutoConfiguration;
import io.awspring.cloud.autoconfigure.core.RegionProviderAutoConfiguration;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.KmsClient;

@AutoConfiguration
@ConditionalOnClass({ KmsClient.class, KmsAsyncClient.class })
@EnableConfigurationProperties({ KmsProperties.class })
@AutoConfigureAfter({ CredentialsProviderAutoConfiguration.class, RegionProviderAutoConfiguration.class })
@ConditionalOnProperty(name = "spring.cloud.aws.kms.enabled", havingValue = "true", matchIfMissing = true)
public class KmsAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public KmsClient kmsClient(KmsProperties properties, AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsClientCustomizer> kmsClientCustomizers,
			ObjectProvider<AwsSyncClientCustomizer> awsSyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureSyncClient(KmsClient.builder(),
				properties,
				connectionDetails.getIfAvailable(),
				kmsClientCustomizers.orderedStream(),
				awsSyncClientCustomizers.orderedStream())
				.build();
	}

	@ConditionalOnMissingBean
	@Bean
	public KmsAsyncClient kmsAsyncClient(KmsProperties properties, AwsClientBuilderConfigurer awsClientBuilderConfigurer,
			ObjectProvider<AwsConnectionDetails> connectionDetails,
			ObjectProvider<KmsAsyncClientCustomizer> kmsAsyncClientCustomizers,
			ObjectProvider<AwsAsyncClientCustomizer> awsAsyncClientCustomizers) {
		return awsClientBuilderConfigurer.configureAsyncClient(KmsAsyncClient.builder(),
				properties,
				connectionDetails.getIfAvailable(),
				kmsAsyncClientCustomizers.orderedStream(),
				awsAsyncClientCustomizers.orderedStream()).build();
	}

}
