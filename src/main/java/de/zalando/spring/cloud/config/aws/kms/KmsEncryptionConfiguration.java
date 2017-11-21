package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.cloud.aws.core.config.AmazonWebserviceClientFactoryBean;
import org.springframework.cloud.aws.core.region.RegionProvider;
import org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import com.amazonaws.services.kms.AWSKMS;

import java.util.Optional;

/**
 * This config must be applied to the bootstrap context, which is done by META-INF/spring.factories.<br/>
 * The properties here can be configured in bootstrap.[yml|xml|properties], but not in application.[yml]xml|properties]
 */
@Configuration
@ConditionalOnProperty(prefix = "aws.kms", name = "enabled", havingValue = "true", matchIfMissing = true)
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

        /**
         * The ARN of the KMS key, e.g. arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031
         */
        @Value("${aws.kms.keyId:}")
        private String kmsKeyId;

        private final AWSKMS kms;

        @Autowired
        public KmsTextEncryptorConfiguration(AWSKMS kms) {
            this.kms = kms;
        }

        @Bean
        KmsTextEncryptor kmsTextEncryptor() {
            return new KmsTextEncryptor(kms, kmsKeyId);
        }
    }

    @Configuration
    @ConditionalOnMissingBean(AWSKMS.class)
    static class KmsConfiguration {

        @Bean
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public AmazonWebserviceClientFactoryBean<AWSKMSClient> kmsFactoryBean(
                final Optional<AWSCredentialsProvider> awsCredentialsProvider,
                final Optional<RegionProvider> regionProvider) {
            return new AmazonWebserviceClientFactoryBean<>(
                    AWSKMSClient.class,
                    awsCredentialsProvider.orElse(null),
                    regionProvider.orElse(null));
        }

    }
}
