package de.zalando.spring.cloud.config.aws.kms;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.AWSKMSClientBuilder;

/**
 * This config must be applied to the bootstrap context, which is done by META-INF/spring.factories.<br/>
 * The properties here can be configured in bootstrap.[yml|xml|properties], but not in application.[yml]xml|properties]
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(KmsProperties.class)
class KmsEncryptionConfiguration {

    private final KmsTextEncryptor kmsTextEncryptor;

    @Autowired
    public KmsEncryptionConfiguration(KmsTextEncryptor kmsTextEncryptor) {
        this.kmsTextEncryptor = kmsTextEncryptor;
    }

    @Bean
    EnvironmentDecryptApplicationInitializer environmentDecryptApplicationInitializer() {
        return new EnvironmentDecryptApplicationInitializer(kmsTextEncryptor);
    }

    @Configuration
    static class KmsTextEncryptorConfiguration {

        private final KmsProperties properties;

        private final AWSKMS kms;

        @Autowired
        public KmsTextEncryptorConfiguration(KmsProperties properties, AWSKMS kms) {
            this.properties = properties;
            this.kms = kms;
        }

        @Bean
        KmsTextEncryptor kmsTextEncryptor() {
            return new KmsTextEncryptor(kms, properties.getKeyId());
        }
    }
    
    @Configuration
    @ConditionalOnProperty(name="aws.kms.endpoint.service-endpoint")
    @ConditionalOnMissingBean(AWSKMS.class)
    static class KmsConfigurationEndpoint {

        private final KmsProperties properties;

        @Autowired
        public KmsConfigurationEndpoint(KmsProperties properties) {
            this.properties = properties;
        }

        @Bean
        public AWSKMS kms() {
            final AWSKMSClientBuilder builder = AWSKMSClient.builder();
           	builder.withEndpointConfiguration(new EndpointConfiguration(properties.getEndpoint().getServiceEndpoint(), properties.getEndpoint().getSigningRegion()));
            return builder.build();
        }

    }

    @Configuration
    @ConditionalOnProperty(name="aws.kms.region", matchIfMissing=true)
    @ConditionalOnMissingBean(AWSKMS.class)
    static class KmsConfigurationRegion {

        private final KmsProperties properties;

        @Autowired
        public KmsConfigurationRegion(KmsProperties properties) {
            this.properties = properties;
        }

        @Bean
        public AWSKMS kms() {
            final AWSKMSClientBuilder builder = AWSKMSClient.builder();
            Optional.ofNullable(properties.getRegion()).ifPresent(builder::setRegion);
            return builder.build();
        }

    }
}
