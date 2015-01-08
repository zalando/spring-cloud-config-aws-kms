Spring Cloud Config - AWS KMS Add-on
====================================

This is a Spring Cloud Config add-on that provides encryption via AWS (Amazon Web Services) KMS (Key management service).

Installation
------------

### Prerequisites
Given you have a [Spring Boot](http://projects.spring.io/spring-boot/) application.

### Step 1
Add dependencies to you pom. You will need a version of `org.springframework.cloud:spring-cloud-config-client`

    ...
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-client</artifactId>
        <version>${spring-cloud.version}</version>
    </dependency>
    
    <dependency>
        <groupId>de.zalando</groupId>
        <artifactId>spring-cloud-config-aws-kms</artifactId>
        <version>${spring-cloud-aws-kms.version}</version>
    </dependency>
    ...

### Step 2
Configure bootstrap properties. See [Spring Cloud Config Reference](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_the_bootstrap_application_context)
for details. 

E.g. `bootstrap.yml`:

    aws:
        region: eu-west-1
        kms:
            keyId: arn:aws:kms:eu-west-1:089972051332:key/9d9fca31-54c5-4df5-ba4f-127dfb9a5031

Usage
-----

Now you can add encrypted values to you property files. An encrypted value must always start with `{cipher}`.
Those properties are automatically decrypted on application startup.

E.g. `application.yml`

    secretPassword: '{cipher}CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

Hints
-----

### How to get the cipher text?

The Spring Cloud Config Server library provides an endpoint to encrypt plain text strings.
See [reference](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_encryption_and_decryption) for details.

Or you can use the our small [CLI tool](https://github.com/zalando/spring-cloud-config-aws-kms/tree/master/encryption-cli).
