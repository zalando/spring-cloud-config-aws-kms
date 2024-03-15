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
     * Must match the `name` property of one enum entry of {@link software.amazon.awssdk.regions.Region}. If not set, the
     * <a href="http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment">
     * Default Region Provider Chain</a> of the AWS SDK is used.
     */
    private String region;

    /**
     * <strong>Optional</strong> service endpoint and signing region of AWS KMS that you would like to route to.
     * If provided, must supply either a custom created VPC Endpoint or one of the KMS Endpoints listed <a href="https://docs.aws.amazon.com/general/latest/gr/rande.html#kms_region">here</a>.
     * In the event that both region and endpoint properties are both supplied, region will be ignored as region is derived from the service endpoint.
     */
    private String endpointOverride;

    /**
     * <strong>Optional</strong> encryption algorithm, that should be used for `encrypt` and `decrypt` operations.
     * For possible values see {@link software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec}
     */
    private String encryptionAlgorithm = "SYMMETRIC_DEFAULT";

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
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEndpointOverride() {
        return endpointOverride;
    }

    public void setEndpointOverride(String endpointOverride) {
        this.endpointOverride = endpointOverride;
    }
}
