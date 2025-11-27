package org.zalando.awsspring.cloud.autoconfigure.kms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.zalando.awsspring.cloud.autoconfigure.ConfiguredAwsClient;

import io.awspring.cloud.autoconfigure.core.AwsAutoConfiguration;
import io.awspring.cloud.autoconfigure.core.CredentialsProviderAutoConfiguration;
import io.awspring.cloud.autoconfigure.core.RegionProviderAutoConfiguration;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.KmsClient;

/**
 * Tests for {@link KmsAutoConfiguration}.
 */
class KmsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues("spring.cloud.aws.region.static:eu-west-1")
            .withConfiguration(AutoConfigurations.of(RegionProviderAutoConfiguration.class,
                    CredentialsProviderAutoConfiguration.class, KmsAutoConfiguration.class,
                    AwsAutoConfiguration.class));

    @Test
    void kmsAutoConfigurationIsDisabled() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.kms.enabled:false")
                .run(context -> assertThat(context).doesNotHaveBean(KmsClient.class));
    }

    @Test
    void kmsAutoConfigurationIsDisabledWhenKmsModuleIsNotInClassPath() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(KmsClient.class))
                .run(context -> assertThat(context).doesNotHaveBean(KmsClient.class));
    }

    @Test
    void kmsAutoConfigurationIsEnabled() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.kms.enabled:true").run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            assertThat(context).hasSingleBean(KmsAsyncClient.class);
            assertThat(context).hasSingleBean(KmsProperties.class);
            assertThat(context).hasBean("kmsClient");
            assertThat(context).hasBean("kmsAsyncClient");
            ConfiguredAwsClient client = new ConfiguredAwsClient(context.getBean(KmsClient.class));
            assertThat(client.getEndpoint()).isEqualTo(URI.create("https://kms.eu-west-1.amazonaws.com"));
        });
    }

    @Test
    void createsKmsClientBeansByDefault() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            assertThat(context).hasSingleBean(KmsAsyncClient.class);
            assertThat(context).hasSingleBean(KmsProperties.class);
        });
    }

    @Test
    void withCustomEndpoint() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.kms.endpoint:http://localhost:8090").run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            ConfiguredAwsClient client = new ConfiguredAwsClient(context.getBean(KmsClient.class));
            assertThat(client.getEndpoint()).isEqualTo(URI.create("http://localhost:8090"));
            assertThat(client.isEndpointOverridden()).isTrue();
        });
    }

    @Test
    void withCustomGlobalEndpoint() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.endpoint:http://localhost:9090").run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            ConfiguredAwsClient client = new ConfiguredAwsClient(context.getBean(KmsClient.class));
            assertThat(client.getEndpoint()).isEqualTo(URI.create("http://localhost:9090"));
            assertThat(client.isEndpointOverridden()).isTrue();
        });
    }

    @Test
    void withCustomAsyncEndpoint() {
        this.contextRunner.withPropertyValues("spring.cloud.aws.kms.endpoint:http://localhost:8090").run(context -> {
            assertThat(context).hasSingleBean(KmsAsyncClient.class);
            ConfiguredAwsClient client = new ConfiguredAwsClient(context.getBean(KmsAsyncClient.class));
            assertThat(client.getEndpoint()).isEqualTo(URI.create("http://localhost:8090"));
            assertThat(client.isEndpointOverridden()).isTrue();
        });
    }

    @Test
    void usesCustomSyncKmsClientBeanWhenProvided() {
        this.contextRunner.withUserConfiguration(CustomKmsClientConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            assertThat(context.getBean(KmsClient.class)).isSameAs(context.getBean("customKmsClient"));
        });
    }

    @Test
    void usesCustomAsyncKmsClientBeanWhenProvided() {
        this.contextRunner.withUserConfiguration(CustomKmsAsyncClientConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(KmsAsyncClient.class);
            assertThat(context.getBean(KmsAsyncClient.class)).isSameAs(context.getBean("customKmsAsyncClient"));
        });
    }

    @Test
    void withSyncClientCustomizer() {
        this.contextRunner.withUserConfiguration(SyncClientCustomizerConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(KmsClient.class);
            assertThat(context).hasSingleBean(KmsClientCustomizer.class);
        });
    }

    @Test
    void withAsyncClientCustomizer() {
        this.contextRunner.withUserConfiguration(AsyncClientCustomizerConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(KmsAsyncClient.class);
            assertThat(context).hasSingleBean(KmsAsyncClientCustomizer.class);
        });
    }

    @TestConfiguration
    static class CustomKmsClientConfiguration {
        @Bean
        KmsClient customKmsClient() {
            return mock(KmsClient.class);
        }
    }

    @TestConfiguration
    static class CustomKmsAsyncClientConfiguration {
        @Bean
        KmsAsyncClient customKmsAsyncClient() {
            return mock(KmsAsyncClient.class);
        }
    }

    @TestConfiguration
    static class SyncClientCustomizerConfiguration {
        @Bean
        KmsClientCustomizer kmsClientCustomizer() {
            return builder -> {
                // Custom configuration
            };
        }
    }

    @TestConfiguration
    static class AsyncClientCustomizerConfiguration {
        @Bean
        KmsAsyncClientCustomizer kmsAsyncClientCustomizer() {
            return builder -> {
                // Custom configuration
            };
        }
    }
}
