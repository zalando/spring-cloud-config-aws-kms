package de.zalando.spring.cloud.config.aws.kms;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static de.zalando.spring.cloud.config.aws.kms.OutputMode.BASE64;
import static de.zalando.spring.cloud.config.aws.kms.OutputMode.PLAIN;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

public class EncryptedTokenTest {

    private static final KmsTextEncryptorOptions BASE64_OPTIONS = KmsTextEncryptorOptions.builder().withOutputMode(BASE64).build();
    private static final KmsTextEncryptorOptions PLAIN_OPTIONS = KmsTextEncryptorOptions.builder().withOutputMode(PLAIN).build();
    private static final Map<String, String> EMPTY_MAP = emptyMap();
    private static final Map<String, String> CONTEXT_MAP;

    static {
        CONTEXT_MAP = new HashMap<>();
        CONTEXT_MAP.put("param", "L’homme c’est rien");
        CONTEXT_MAP.put("test", "l’oeuvre c’est tout");
        CONTEXT_MAP.put("valueless", "");
    }

    public static Stream<Arguments> data() {
        return Stream.of(
                Arguments.of("(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)[foo=bar,output=base64]SGVsbG8gV29ybGQ=", BASE64_OPTIONS, CONTEXT_MAP),
                Arguments.of("[foo=bar,output=base64](param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)SGVsbG8gV29ybGQ=", BASE64_OPTIONS, CONTEXT_MAP),
                Arguments.of("(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, CONTEXT_MAP),
                Arguments.of("[foo=bar,output=base64]SGVsbG8gV29ybGQ=", BASE64_OPTIONS, EMPTY_MAP),
                Arguments.of("SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP),
                Arguments.of("()[]SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP),
                Arguments.of("[]()SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP),
                Arguments.of("()SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP),
                Arguments.of("[]SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP),
                Arguments.of("SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP)
        );
    }

    @ParameterizedTest(name = "{index}: fib[{0}]={1}")
    @MethodSource("data")
    public void testParseToken(String testString, KmsTextEncryptorOptions expectedOptions, Map<String, String> expectedContext) {
        final EncryptedToken token = EncryptedToken.parse(testString);

        assertThat(token.getCipherBytes()).isEqualTo(ByteBuffer.wrap("Hello World".getBytes()));
        assertThat(token.getOptions()).isEqualTo(expectedOptions);
        assertThat(token.getEncryptionContext()).isEqualTo(expectedContext);
    }
}
