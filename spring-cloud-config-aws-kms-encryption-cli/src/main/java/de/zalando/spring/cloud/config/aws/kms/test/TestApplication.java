package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@EnableConfigurationProperties({EncryptProperties.class, DecryptProperties.class})
public class TestApplication {

    public static void main(final String[] args) {
        final SpringApplicationBuilder app = new SpringApplicationBuilder(TestApplication.class);
        app.bannerMode(OFF);
        app.run(args);
    }
}
