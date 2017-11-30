package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("decrypt")
public class DecryptProperties {

    private String cipherText;

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }
}
