package org.zalando.awsspring.cloud.bootstrap.encrypt;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.zalando.awsspring.cloud.bootstrap.encrypt.EncryptedToken;
import org.zalando.awsspring.cloud.bootstrap.encrypt.EncryptedTokenOptions;
import org.zalando.awsspring.cloud.bootstrap.encrypt.OutputMode;

public class EncryptedTokenTest {
	
	private static String CIPHER_BASE64 = Base64.getEncoder().encodeToString("Hello World".getBytes());
	
	private static Map<String, String> CONTEXT_MAP;
	
    static {
        CONTEXT_MAP = new HashMap<>();
        CONTEXT_MAP.put("param", "L’homme c’est rien");
        CONTEXT_MAP.put("test", "l’oeuvre c’est tout");
        CONTEXT_MAP.put("valueless", "");
    }
	
	public static Stream<Arguments> data() {
		return Stream.of(
			Arguments.of(CIPHER_BASE64, null, null),
			Arguments.of("[]" + CIPHER_BASE64, null, new EncryptedTokenOptions(null, null)),
			Arguments.of("()" + CIPHER_BASE64, Collections.emptyMap(), null),
			Arguments.of("[]()" + CIPHER_BASE64, Collections.emptyMap(), new EncryptedTokenOptions(null, null)),
			Arguments.of("()[]" + CIPHER_BASE64, Collections.emptyMap(), new EncryptedTokenOptions(null, null)),
			Arguments.of("[keyId=sample-key,encryptionAlgorithm=SYMMETRIC_DEFAULT,output=base64,foo=bar]" + CIPHER_BASE64, null, new EncryptedTokenOptions("sample-key", "SYMMETRIC_DEFAULT", OutputMode.BASE64)),
			Arguments.of("(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)" + CIPHER_BASE64, CONTEXT_MAP, null),
			Arguments.of("(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)[output=base64]" + CIPHER_BASE64, CONTEXT_MAP, new EncryptedTokenOptions(null, null, OutputMode.BASE64)),
			Arguments.of("[output=base64](param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)" + CIPHER_BASE64, CONTEXT_MAP, new EncryptedTokenOptions(null, null, OutputMode.BASE64))
		);
	}

	@ParameterizedTest
	@MethodSource("data")
	public void testParseToken(String tokenString, Map<String, String> expectedContext, EncryptedTokenOptions expectedOptions) {
		EncryptedToken token = EncryptedToken.parse(tokenString);
		
		Assertions.assertThat(token.getCipher()).isEqualTo("Hello World".getBytes());
		Assertions.assertThat(token.getContext()).isEqualTo(expectedContext);
		Assertions.assertThat(token.getOptions()).isEqualTo(expectedOptions);
	}
}
