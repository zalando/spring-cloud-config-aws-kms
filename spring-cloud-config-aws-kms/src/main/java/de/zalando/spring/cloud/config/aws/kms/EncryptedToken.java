package de.zalando.spring.cloud.config.aws.kms;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

class EncryptedToken {

    private static final Pattern ENCRYPTED_STRING = Pattern.compile("^(?>\\((?<context>.*)\\)|\\[(?<options>.*)]){0,2}(?<cipher>.*)$");
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final Function<String, String> BASE64_DECODE_VALUE = v -> new String(BASE64_DECODER.decode(v));
    private static final Function<String[], String> FIRST = arr -> arr[0];
    private static final Function<String[], String> SECOND = arr -> arr.length > 1 ? arr[1] : "";
    private static final String PAIR_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final ByteBuffer cipherBytes;
    private final Map<String, String> encryptionContext;
    private final KmsTextEncryptorOptions options;

    private EncryptedToken(ByteBuffer cipherBytes, Map<String, String> encryptionContext, KmsTextEncryptorOptions options) {
        this.cipherBytes = cipherBytes;
        this.encryptionContext = encryptionContext;
        this.options = options;
    }

    ByteBuffer getCipherBytes() {
        return cipherBytes;
    }

    Map<String, String> getEncryptionContext() {
        return encryptionContext;
    }

    KmsTextEncryptorOptions getOptions() {
        return options;
    }

    static EncryptedToken parse(String s) {
        Assert.hasText(s, "Encrypted string must not be blank");

        final Matcher matcher = ENCRYPTED_STRING.matcher(s);
        Assert.isTrue(matcher.matches(), "Malformed encrypted string '" + s + "'");

        final String contextString = matcher.group("context");
        final String optionsString = matcher.group("options");
        final String cipherString = matcher.group("cipher");

        return new EncryptedToken(parseCipher(cipherString), parseContext(contextString), parseOptions(optionsString));
    }

    private static ByteBuffer parseCipher(String valueString) {
        return ByteBuffer.wrap(BASE64_DECODER.decode(valueString.getBytes()));
    }

    private static KmsTextEncryptorOptions parseOptions(String optionsString) {
        final Map<String, String> optionsMap = parseKeyValueMap(optionsString);

        final OutputMode output = OutputMode.valueOf(
                Optional.ofNullable(optionsMap.get("output"))
                        .map(String::toUpperCase)
                        .orElse(OutputMode.PLAIN.name()));
        final String kmsKeyId = optionsMap.get("keyId");
        final String encryptionAlgorithm = optionsMap.get("algorithm");

        return new KmsTextEncryptorOptions(output, kmsKeyId, encryptionAlgorithm);
    }

    private static Map<String, String> parseContext(String contextString) {
        return parseKeyValueMap(contextString, BASE64_DECODE_VALUE);
    }

    /**
     * Convenience overload of {@link #parseKeyValueMap(String, Function)} that keeps the original map values without
     * modifying them
     */
    private static Map<String, String> parseKeyValueMap(String kvString) {
        return parseKeyValueMap(kvString, identity());
    }

    /**
     * Parses a key-value pair string such as "param1=WdfaA,param2=AZrr,param3" into a map.
     *
     * <p>Keys with no value are assigned an empty string for convenience. The `valueMapper` function is applied to
     * all values of the map</p>
     *
     * @param kvString    a string containing key value pairs. Multiple pairs are separated by comma ','
     *                    and the keys are separated from the values by the equal sign "="
     * @param valueMapper a function that will be applied to the parsed values. Use Functions.identity to keep the original value.
     * @return a String-String Map
     */
    private static Map<String, String> parseKeyValueMap(String kvString, Function<String, String> valueMapper) {
        return Stream.of(
                Optional.ofNullable(kvString)
                        .map(StringUtils::trimAllWhitespace)
                        .filter(StringUtils::hasText)
                        .map(s -> s.split(PAIR_SEPARATOR))
                        .orElse(new String[0]))
                .map(StringUtils::trimAllWhitespace)
                .map(pair -> pair.split(KEY_VALUE_SEPARATOR, 2))
                .collect(toMap(
                        FIRST.andThen(StringUtils::trimAllWhitespace),
                        SECOND.andThen(StringUtils::trimAllWhitespace).andThen(valueMapper)));
    }
}
