package de.zalando.spring.cloud.config.aws.kms.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
public class TestApplication {

    public static void main(final String[] args) {
        final SpringApplicationBuilder app = new SpringApplicationBuilder(TestApplication.class);
        app.bannerMode(OFF);
        app.properties("spring.cloud.bootstrap.enabled=false", "logging.level.ROOT=WARN");
        app.run(args);
    }
}
