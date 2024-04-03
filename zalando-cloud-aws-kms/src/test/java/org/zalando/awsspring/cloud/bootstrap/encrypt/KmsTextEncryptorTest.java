package org.zalando.awsspring.cloud.bootstrap.encrypt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.zalando.awsspring.cloud.bootstrap.encrypt.KmsTextEncryptor;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateKeyRequest;
import software.amazon.awssdk.services.kms.model.CreateKeyResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KeySpec;
import software.amazon.awssdk.services.kms.model.KeyUsageType;

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
}
