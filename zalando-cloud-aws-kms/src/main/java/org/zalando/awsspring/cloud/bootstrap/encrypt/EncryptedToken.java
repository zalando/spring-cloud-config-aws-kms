package org.zalando.awsspring.cloud.bootstrap.encrypt;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class EncryptedToken {
	
	private static final Pattern ENCRYPTED_TOKEN_PATTERN = Pattern.compile("^(?>\\((?<context>.*)\\)|\\[(?<options>.*)]){0,2}(?<cipher>.*)$");

	private byte[] cipher;
	
	private Map<String, String> context;
	
	private EncryptedTokenOptions options;
	
	private EncryptedToken(byte[] cipher, Map<String, String> context, EncryptedTokenOptions options) {
		this.cipher = cipher;
		this.context = context;
		this.options = options;
	}
	
	public byte[] getCipher() {
		return cipher;
	}
	
	public Map<String, String> getContext() {
		return context;
	}
	
	public EncryptedTokenOptions getOptions() {
		return options;
	}
	
	public static EncryptedToken parse(String text) {
		Matcher matcher = ENCRYPTED_TOKEN_PATTERN.matcher(text);
		boolean matches = matcher.matches();
		Assert.isTrue(matches, "Malformed encrypted string '" + text + "'");

		String contextString = matcher.group("context");
        String optionsString = matcher.group("options");
        String cipherString = matcher.group("cipher");
        
        byte[] cipher = parseCipher(cipherString);
        Map<String, String> context = parseContext(contextString);
        EncryptedTokenOptions options = parseOptions(optionsString);
        
        return new EncryptedToken(cipher, context, options);
	}
	
	private static byte[] parseCipher(String cipherString) {
		return Base64.getDecoder().decode(cipherString);
	}
	
	private static Map<String, String> parseContext(String contextString) {
		return parseMap(contextString, value -> new String(Base64.getDecoder().decode(value)));
	}
	
	private static EncryptedTokenOptions parseOptions(String optionsString) {
		Map<String, String> options = parseMap(optionsString, Function.identity());
		if (options == null) {
			return null;
		}
		
		String keyId = options.get("keyId");
		String encryptionAlgorithm = options.get("encryptionAlgorithm");
		String modeText = options.get("output");
		
		if (modeText != null) {
			return new EncryptedTokenOptions(keyId, encryptionAlgorithm, OutputMode.valueOf(modeText.toUpperCase()));
		} else {
			return new EncryptedTokenOptions(keyId, encryptionAlgorithm);
		}
	}
	
	private static Map<String, String> parseMap(String text, Function<String, String> transformer) {
		if (text == null) {
			return null;
		}
		
		Map<String, String> result = new HashMap<>();
		String delimiter = ",";
		int pos = 0;
		int delPos;
		while ((delPos = text.indexOf(delimiter, pos)) != -1 ) {
			String kvString = text.substring(pos, delPos).strip();
			String[] kv = kvString.split("=", 2);
			
			result.put(kv[0], kv.length == 1 ? "" : transformer.apply(kv[1]));
			
			pos = delPos + delimiter.length();
		}
		if (text.length() > 0 && pos <= text.length()) {
			String kvString = text.substring(pos).strip();
			String[] kv = kvString.split("=", 2);
			result.put(kv[0].strip(), kv.length == 1 ? "" : transformer.apply(kv[1]));
		}
		return result;
	}
}
