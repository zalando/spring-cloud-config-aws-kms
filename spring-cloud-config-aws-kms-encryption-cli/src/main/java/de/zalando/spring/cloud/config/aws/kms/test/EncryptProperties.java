package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("encrypt")
public class EncryptProperties {

    private String plaintext;

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }
}
