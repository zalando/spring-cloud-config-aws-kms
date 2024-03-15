package com.zalando.awsspring.cloud.autoconfigure.kms;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.awspring.cloud.autoconfigure.AwsClientProperties;

@ConfigurationProperties(prefix = KmsProperties.PREFIX)
public class KmsProperties extends AwsClientProperties {

	public static final String PREFIX = "spring.cloud.aws.kms";

}
