Spring Cloud Config - AWS KMS Add-on
====================================

[![Build Status](https://travis-ci.org/zalando/spring-cloud-config-aws-kms.svg?branch=master)](https://travis-ci.org/zalando/spring-cloud-config-aws-kms)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.zalando/spring-cloud-config-aws-kms/badge.svg)](http://www.javadoc.io/doc/org.zalando/spring-cloud-config-aws-kms)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/spring-cloud-config-aws-kms.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/spring-cloud-config-aws-kms)
[![License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://raw.githubusercontent.com/zalando-incubator/aws-support-spring-boot-starter/master/LICENSE)

This is a Spring Cloud Config add-on that provides encryption via AWS (Amazon Web Services) KMS (Key management service).

Please see our related [aws-support-spring-boot-starter](https://github.com/zalando-incubator/aws-support-spring-boot-starter), a minimal Spring-Boot-Starter that detects whether an application is running on AWS (or not) and exposes some properties. 

Installation
------------

### Prerequisites
Given you have a [Spring Boot](http://projects.spring.io/spring-boot/) application.

### Step 1
Add dependency to you pom. This includes `org.springframework.cloud:spring-cloud-config-client`

    ...
    <dependency>
        <groupId>org.zalando</groupId>
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

The *aws.kms.keyId* property is required only if you intend to encrypt values in your application. The following contains the properties used by this library.

- aws.region (required)
- aws.kms.keyId
- aws.kms.enabled (defaults to true)

Usage
-----

Now you can add encrypted values to you property files. An encrypted value must always start with `{cipher}`.
Those properties are automatically decrypted on application startup.

E.g. `application.yml`

    secretPassword: '{cipher}CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

Hints
-----

### How to get the cipher text?

The Spring Cloud Config Server library provides an endpoint to encrypt plain text strings. Make sure to secure this endpoint properly!
See [reference](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_encryption_and_decryption) for details.

You can also use [AWS CLI](http://docs.aws.amazon.com/cli/latest/reference/kms/encrypt.html#examples) or our small
[CLI tool](https://github.com/zalando/spring-cloud-config-aws-kms/tree/master/encryption-cli) to encrypt and decrypt values.


Contribution
------------

### Release to Maven Central

    mvn clean release:prepare -Prelease,sonatype -Dresume=false

    mvn release:perform -Prelease,sonatype


License
-------

Copyright (C) 2015 Zalando SE (https://tech.zalando.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
