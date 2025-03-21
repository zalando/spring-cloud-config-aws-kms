package org.zalando.awsspring.cloud.bootstrap.encrypt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Testcontainers
public class KmsTextEncryptorTest {

    @Container
    private static final LocalStackContainer localstack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:latest")).withServices(Service.KMS);

    private static KmsClient kmsClient;

    private static String symmetricKeyId;

    private static String rsaKeyId;

    @BeforeAll
    public static void beforeAll() {
        kmsClient = KmsClient.builder().endpointOverride(localstack.getEndpoint())
            .credentialsProvider(StaticCredentialsProvider
                .create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion())).build();

        CreateKeyResponse response = kmsClient.createKey(CreateKeyRequest.builder().keySpec(KeySpec.SYMMETRIC_DEFAULT)
            .keyUsage(KeyUsageType.ENCRYPT_DECRYPT).build());
        symmetricKeyId = response.keyMetadata().keyId();

        response = kmsClient.createKey(
            CreateKeyRequest.builder().keySpec(KeySpec.RSA_4096).keyUsage(KeyUsageType.ENCRYPT_DECRYPT).build());
        rsaKeyId = response.keyMetadata().keyId();
    }

    @Test
    public void encryptSymmetric() throws Exception {
        KmsTextEncryptor encryptor = new KmsTextEncryptor(kmsClient, symmetricKeyId, null);
        String encrypted = encryptor.encrypt("secret");

        Assertions.assertThat(encrypted).isNotBlank().isBase64();
    }

    @Test
    public void encryptRsa() throws Exception {
        KmsTextEncryptor encryptor = new KmsTextEncryptor(kmsClient, rsaKeyId, null);
        String encrypted = encryptor.encrypt("secret");

        Assertions.assertThat(encrypted).isNotBlank().isBase64();
    }

    @Test
    public void decryptSymmetric() throws Exception {
        String password = "secret";

        EncryptResponse response = kmsClient.encrypt(EncryptRequest.builder().keyId(symmetricKeyId)
            .plaintext(SdkBytes.fromString(password, StandardCharsets.ISO_8859_1)).build());
        String encrypted = Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());

        KmsTextEncryptor encryptor = new KmsTextEncryptor(kmsClient, symmetricKeyId, null);
        String plaintext = encryptor.decrypt(encrypted);

        Assertions.assertThat(plaintext).isEqualTo(password);
    }

    @Test
    public void decryptRsa() throws Exception {
        String password = "secret";

        EncryptResponse response = kmsClient.encrypt(EncryptRequest.builder().keyId(rsaKeyId)
            .plaintext(SdkBytes.fromString(password, StandardCharsets.ISO_8859_1)).build());
        String encrypted = Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());

        KmsTextEncryptor encryptor = new KmsTextEncryptor(kmsClient, rsaKeyId, null);
        String plaintext = encryptor.decrypt(encrypted);

        Assertions.assertThat(plaintext).isEqualTo(password);
    }

    @Test
    public void decryptRsaByExtraOptions() throws Exception {
        KmsClient spyKmsClient = spy(kmsClient);
        String password = "secret";
		EncryptionAlgorithmSpec encryptionAlgorithm = EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256;
        Map<String, String> context = Map.of("Code", "Context");

        EncryptResponse response = spyKmsClient.encrypt(EncryptRequest.builder().keyId(rsaKeyId)
            .encryptionAlgorithm(encryptionAlgorithm)
            .encryptionContext(context)
            .plaintext(SdkBytes.fromString(password, StandardCharsets.ISO_8859_1)).build());
        String encrypted = Base64.getEncoder().encodeToString(response.ciphertextBlob().asByteArray());

        String contextStr = context.entrySet().stream()
            .map(e -> e.getKey() + "=" + Base64.getEncoder().encodeToString(e.getValue().getBytes()))
            .collect(Collectors.joining(","));
        String encryptedOptions = String.format("(%s)[encryptionAlgorithm=%s,keyId=%s]%s", contextStr, encryptionAlgorithm, rsaKeyId, encrypted);

        KmsTextEncryptor encryptor = new KmsTextEncryptor(spyKmsClient, null, null);
        String plaintext = encryptor.decrypt(encryptedOptions);

        ArgumentCaptor<DecryptRequest> captor = ArgumentCaptor.forClass(DecryptRequest.class);
        verify(spyKmsClient).decrypt(captor.capture());

		Assertions.assertThat(captor.getValue().keyId()).isEqualTo(rsaKeyId);
		Assertions.assertThat(captor.getValue().encryptionAlgorithm()).isEqualTo(encryptionAlgorithm);
        Assertions.assertThat(captor.getValue().encryptionContext()).isEqualTo(context);
		Assertions.assertThat(plaintext).isEqualTo(password);
    }
}
