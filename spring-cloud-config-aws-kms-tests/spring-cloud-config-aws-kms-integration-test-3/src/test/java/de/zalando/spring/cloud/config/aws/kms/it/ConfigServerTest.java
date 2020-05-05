package de.zalando.spring.cloud.config.aws.kms.it;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.RequestEntity.post;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigServerTest.TestApp.class)
public class ConfigServerTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private AWSKMS mockKms;

    private final BasicJsonTester json = new BasicJsonTester(getClass());

    @Test
    void testGetConfigFromServer() {
        final ResponseEntity<String> response = rest.getForEntity("/my-test-app/default", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final JsonContent<?> jsonBody = json.from(response.getBody());
        System.out.println(jsonBody.getJson());
        assertThat(jsonBody).extractingJsonPathValue("$.name")
                .isEqualTo("my-test-app");
        assertThat(jsonBody).extractingJsonPathArrayValue("$.profiles")
                .containsExactly("default");
        assertThat(jsonBody).extractingJsonPathArrayValue("$.propertySources..source['info.foo']")
                .containsExactly("bar");
        assertThat(jsonBody).extractingJsonPathArrayValue("$.propertySources..source['top.secret']")
                .containsExactly("Hello World");

        final DecryptRequest expectedRequest = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(Base64.getDecoder().decode("c2VjcmV0".getBytes())));
        verify(mockKms, atLeastOnce()).decrypt(eq(expectedRequest));
    }

    @Test
    void testEncryptEndpoint() {
        final String plainText = "some-plaintext";
        final String cipherText = "cIpHeR";

        doAnswer(invocation -> new EncryptResult().withCiphertextBlob(ByteBuffer.wrap(cipherText.getBytes())))
                .when(mockKms).encrypt(any(EncryptRequest.class));

        final ResponseEntity<String> response = rest.exchange(
                post(URI.create("/encrypt"))
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .body(plainText),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Base64.getEncoder().encodeToString(cipherText.getBytes()));
    }

    @Test
    void testDecryptEndpoint() {
        final String cipherText = Base64.getEncoder().encodeToString("cIpHeR".getBytes());

        // Config Server does a "test" encrypt with the given key
        doAnswer(invocation -> new EncryptResult().withCiphertextBlob(ByteBuffer.wrap(cipherText.getBytes())))
                .when(mockKms).encrypt(any(EncryptRequest.class));

        final ResponseEntity<String> response = rest.exchange(
                post(URI.create("/decrypt"))
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .body(cipherText),
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello World");
    }

    @SpringBootApplication
    @EnableConfigServer
    public static class TestApp {

    }
}
