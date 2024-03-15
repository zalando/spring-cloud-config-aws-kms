package com.zalando.awsspring.samples.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
public class BootstrapApplicationTest {

	static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
			.withServices(Service.KMS);

	public static void overridePropertes(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.aws.region.static", () -> localStack.getRegion());
		registry.add("spring.cloud.aws.credentials.access-key", () -> localStack.getAccessKey());
		registry.add("sprng.cloud.aws.credentials.secret-key", () -> localStack.getSecretKey());
		registry.add("spring.cloud.aws.kms.endpoint", () -> localStack.getEndpointOverride(Service.KMS));
	}
	
	@Test
	public void contextLoads() {

	}
}
