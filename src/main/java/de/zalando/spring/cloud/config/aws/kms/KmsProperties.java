package de.zalando.spring.cloud.config.aws.kms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("aws.kms")
public class KmsProperties {

    /**
     * <strong>Optional</strong> ID or full ARN of the KMS key, e.g.
     * <ul>
     * <li>arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4de5-ba4f-128dfb9a5031, or</li>
     * <li>9d9fca31-54c5-4de5-ba4f-128dfb9a5031</li>
     * </ul>
     * Only needed for encryption of values.
     */
    private String keyId;

    /**
     * <strong>Optional</strong> id of the AWS region of the KMS key that was used for encryption/decryption.
     * Must match the `name` property of one enum entry of {@link com.amazonaws.regions.Regions}. If not set, the
     * <a href="http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment">
     * Default Region Provider Chain</a> of the AWS SDK is used.
     */
    private String region;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
