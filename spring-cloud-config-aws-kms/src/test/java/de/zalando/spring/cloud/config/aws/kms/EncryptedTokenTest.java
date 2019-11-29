package de.zalando.spring.cloud.config.aws.kms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static de.zalando.spring.cloud.config.aws.kms.OutputMode.BASE64;
import static de.zalando.spring.cloud.config.aws.kms.OutputMode.PLAIN;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class EncryptedTokenTest {

    private static final KmsTextEncryptorOptions BASE64_OPTIONS = new KmsTextEncryptorOptions(BASE64, null, null);
    private static final KmsTextEncryptorOptions PLAIN_OPTIONS = new KmsTextEncryptorOptions(PLAIN, null, null);
    private static final Map<String, String> EMPTY_MAP = emptyMap();
    private static final Map<String, String> CONTEXT_MAP;

    static {
        CONTEXT_MAP = new HashMap<>();
        CONTEXT_MAP.put("param", "L’homme c’est rien");
        CONTEXT_MAP.put("test", "l’oeuvre c’est tout");
        CONTEXT_MAP.put("valueless", "");
    }

    private final String testString;
    private final KmsTextEncryptorOptions expectedOptions;
    private final Map<String,String> expectedContext;

    public EncryptedTokenTest(String testString, KmsTextEncryptorOptions expectedOptions, Map<String, String> expectedContext) {
        this.testString = testString;
        this.expectedOptions = expectedOptions;
        this.expectedContext = expectedContext;
    }

    @Parameterized.Parameters(name = "{index}: fib[{0}]={1}")
    public static Iterable<Object[]> data() {
        return asList(new Object[][]{
                {"(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)[foo=bar,output=base64]SGVsbG8gV29ybGQ=", BASE64_OPTIONS, CONTEXT_MAP},
                {"[foo=bar,output=base64](param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)SGVsbG8gV29ybGQ=", BASE64_OPTIONS, CONTEXT_MAP},
                {"(param=TOKAmWhvbW1lIGPigJllc3Qgcmllbg==,test=bOKAmW9ldXZyZSBj4oCZZXN0IHRvdXQ= ,valueless)SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, CONTEXT_MAP},
                {"[foo=bar,output=base64]SGVsbG8gV29ybGQ=", BASE64_OPTIONS, EMPTY_MAP},
                {"SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP},
                {"()[]SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP},
                {"[]()SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP},
                {"()SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP},
                {"[]SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP},
                {"SGVsbG8gV29ybGQ=", PLAIN_OPTIONS, EMPTY_MAP}
        });
    }

    @Test
    public void testParseToken() {
        final EncryptedToken token = EncryptedToken.parse(testString);

        assertThat(token.getCipherBytes()).isEqualTo(ByteBuffer.wrap("Hello World".getBytes()));
        assertThat(token.getOptions()).isEqualTo(expectedOptions);
        assertThat(token.getEncryptionContext()).isEqualTo(expectedContext);
    }
}
