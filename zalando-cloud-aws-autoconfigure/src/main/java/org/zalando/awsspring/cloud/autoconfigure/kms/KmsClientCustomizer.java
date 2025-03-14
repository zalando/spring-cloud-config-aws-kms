package org.zalando.awsspring.cloud.autoconfigure.kms;

import io.awspring.cloud.autoconfigure.AwsClientCustomizer;
import software.amazon.awssdk.services.kms.KmsClientBuilder;

@FunctionalInterface
public interface KmsClientCustomizer extends AwsClientCustomizer<KmsClientBuilder>{

}
