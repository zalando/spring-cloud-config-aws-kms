package org.zalando.awsspring.cloud.autoconfigure.kms;

import io.awspring.cloud.autoconfigure.AwsClientCustomizer;
import software.amazon.awssdk.services.kms.KmsAsyncClientBuilder;

@FunctionalInterface
public interface KmsAsyncClientCustomizer extends AwsClientCustomizer<KmsAsyncClientBuilder> {

}
