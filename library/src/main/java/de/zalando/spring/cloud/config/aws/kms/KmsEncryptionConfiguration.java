package de.zalando.spring.cloud.config.aws.kms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

import java.net.URI;
import java.util.Objects;

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

        private final KmsClient kms;

        @Autowired
        public KmsTextEncryptorConfiguration(KmsProperties properties, KmsClient kms) {
            this.properties = properties;
            this.kms = kms;
        }

        @Bean
        KmsTextEncryptor kmsTextEncryptor() {
            return new KmsTextEncryptor(kms, properties.getKeyId(), properties.getEncryptionAlgorithm());
        }
    }

    @Configuration
    @ConditionalOnMissingBean(KmsClient.class)
    static class KmsConfiguration {

        private final KmsProperties properties;

        @Autowired
        public KmsConfiguration(KmsProperties properties) {
            this.properties = properties;
        }

        @Bean
        public KmsClient kms() {
            KmsClientBuilder builder = KmsClient.builder();

            if (Objects.nonNull(properties.getEndpointOverride())) {
                builder.endpointOverride(URI.create(properties.getEndpointOverride()));
            }
            else if (Objects.nonNull(properties.getRegion())) {
                builder.region(Region.of(properties.getRegion()));
            }

            return builder.build();
        }
    }
}
