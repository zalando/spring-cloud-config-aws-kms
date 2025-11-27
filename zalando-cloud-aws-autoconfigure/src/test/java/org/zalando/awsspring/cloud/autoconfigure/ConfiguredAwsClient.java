package org.zalando.awsspring.cloud.autoconfigure;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

import org.springframework.test.util.ReflectionTestUtils;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.AttributeMap;

public class ConfiguredAwsClient {

    private final AttributeMap clientConfigurationAttributes;

	public ConfiguredAwsClient(SdkClient sdkClient) {
		SdkClientConfiguration clientConfiguration;
		try {
			clientConfiguration = (SdkClientConfiguration) ReflectionTestUtils.getField(sdkClient,
					"clientConfiguration");
		}
		catch (IllegalArgumentException e) {
			// special case for S3CrtAsyncClient
			Object delegate = ReflectionTestUtils.getField(sdkClient, "delegate");
			clientConfiguration = (SdkClientConfiguration) ReflectionTestUtils.getField(delegate,
					"clientConfiguration");
		}
		this.clientConfigurationAttributes = (AttributeMap) ReflectionTestUtils
				.getField(Objects.requireNonNull(clientConfiguration), "attributes");
	}

	public URI getEndpoint() {
		return clientConfigurationAttributes.get(SdkClientOption.ENDPOINT);
	}

	public boolean isEndpointOverridden() {
		return clientConfigurationAttributes.get(SdkClientOption.ENDPOINT_OVERRIDDEN);
	}

	public Region getRegion() {
		return clientConfigurationAttributes.get(AwsClientOption.AWS_REGION);
	}

	public Duration getApiCallTimeout() {
		return clientConfigurationAttributes.get(SdkClientOption.API_CALL_TIMEOUT);
	}

	public Duration getApiCallAttemptTimeout() {
		return clientConfigurationAttributes.get(SdkClientOption.API_CALL_ATTEMPT_TIMEOUT);
	}

	public SdkHttpClient getSyncHttpClient() {
		return clientConfigurationAttributes.get(SdkClientOption.SYNC_HTTP_CLIENT);
	}

	public Boolean getFipsEnabled() {
		return clientConfigurationAttributes.get(AwsClientOption.FIPS_ENDPOINT_ENABLED);
	}

	public Boolean getDualstackEnabled() {
		return clientConfigurationAttributes.get(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED);
	}

	public IdentityProvider getIdentityProviders() {
		return clientConfigurationAttributes.get(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER);
	}

	public DefaultsMode getDefaultsMode() {
		return clientConfigurationAttributes.get(AwsClientOption.DEFAULTS_MODE);
	}

	public SdkAsyncHttpClient getAsyncHttpClient() {
		return clientConfigurationAttributes.get(SdkClientOption.ASYNC_HTTP_CLIENT);
	}

	public AwsCredentialsProvider getAwsCredentialsProvider() {
		return clientConfigurationAttributes.get(AwsClientOption.CREDENTIALS_PROVIDER);
	}

}
