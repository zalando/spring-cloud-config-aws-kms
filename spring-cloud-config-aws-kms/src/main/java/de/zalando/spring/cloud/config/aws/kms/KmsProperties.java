package de.zalando.spring.cloud.config.aws.kms;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.amazonaws.services.kms.model.EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT;

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
     * Must match the `name` property of one enum entry of {@link Regions}. If not set, the
     * <a href="http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment">
     * Default Region Provider Chain</a> of the AWS SDK is used.
     */
    private String region;

    /**
     * <strong>Optional</strong> service endpoint and signing region of AWS KMS that you would like to route to.
     * If provided, must supply either a custom created VPC Endpoint or one of the KMS Endpoints listed <a href="https://docs.aws.amazon.com/general/latest/gr/rande.html#kms_region">here</a>.
     * In the event that both region and endpoint properties are both supplied, region will be ignored as region is derived from the service endpoint.
     */
    private Endpoint endpoint;

    /**
     * <strong>Optional</strong> encryption algorithm, that should be used for `encrypt` and `decrypt` operations.
     */
    private EncryptionAlgorithmSpec encryptionAlgorithm = SYMMETRIC_DEFAULT;

    public static class Endpoint {

        /**
         * <strong>Required<strong> service endpoint, either with or without the protocol (e.g. https://kms.us-west-2.amazonaws.com or kms.us-west-2.amazonaws.com)
         */
        private String serviceEndpoint;

        /**
         * <strong>Optional</strong> signing region. The region to use for SigV4 signing of requests (e.g. us-west-1)
         * In most cases, this can be omitted.  There are use cases where a signing region is also
         * needed and it may be different from the region where the service endpoint lives.
         */
        private String signingRegion;

        public String getServiceEndpoint() {
            return serviceEndpoint;
        }

        public void setServiceEndpoint(String serviceEndpoint) {
            this.serviceEndpoint = serviceEndpoint;
        }

        public String getSigningRegion() {
            return signingRegion;
        }

        public void setSigningRegion(String signingRegion) {
            this.signingRegion = signingRegion;
        }

    }

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

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public EncryptionAlgorithmSpec getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(EncryptionAlgorithmSpec encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }
}
