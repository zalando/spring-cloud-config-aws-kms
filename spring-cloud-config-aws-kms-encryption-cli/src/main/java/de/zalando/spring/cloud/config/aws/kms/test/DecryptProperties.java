package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("decrypt")
public class DecryptProperties {

    private String ciphertext;

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}
