Spring Cloud Config - AWS KMS Add-on
====================================

[![Build Status](https://travis-ci.org/zalando/spring-cloud-config-aws-kms.svg?branch=master)](https://travis-ci.org/zalando/spring-cloud-config-aws-kms)
[![Javadocs](http://javadoc.io/badge/org.zalando/spring-cloud-config-aws-kms.svg?color=blue)](http://javadoc.io/doc/org.zalando/spring-cloud-config-aws-kms)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/spring-cloud-config-aws-kms.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/spring-cloud-config-aws-kms)
[![License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://raw.githubusercontent.com/zalando-incubator/aws-support-spring-boot-starter/master/LICENSE)

This is a Spring Cloud Config add-on that provides encryption via AWS (Amazon Web Services) KMS (Key management service).

Features
--------

* Compatible with Spring Cloud Config
* Spring Boot 2.0-ready
* Supports [custom endpoints](#optional-step-2) for AWS KMS
* Supports AWS KMS [encryption context](#use-an-encryption-context)
* Supports different [output modes](#available-options) for decrypted values
* Minimal dependencies 

Installation
------------

### Prerequisites
Given you have a [Spring Boot](http://projects.spring.io/spring-boot/) application.

**Choose the correct library version!**

Version | Use with
------- | ---------------------------------------
**2.x** | Spring Cloud Edgware + Spring Boot 1.5
**3.x** | Spring Cloud Finchley + Spring Boot 2.0

### Step 1
Add our dependency to your pom.xml (or Gradle build file).

    ...
    <dependency>
        <groupId>org.zalando</groupId>
        <artifactId>spring-cloud-config-aws-kms</artifactId>
        <version>${spring-cloud-aws-kms.version}</version>
    </dependency>
    ...

### Optional: Step 2
Apply configuration to the application's [Bootstrap Context](http://cloud.spring.io/spring-cloud-static/Edgware.RELEASE/single/spring-cloud.html#_the_bootstrap_application_context)

E.g. `bootstrap.yml`:

    aws:
        kms:
            # Optional: only used for encryption
            keyId: 9d9fca31-54c5-4df5-ba4f-127dfb9a5031
            
            # Optional: if not set, the AWS Default Region Provider Chain is used
            region: eu-central-1
            
            # Optional: Turn off the KMS feature completely (e.g. for local development) 
            enabled: false
            
            # Optional: Enable endpoint usage, if provided, aws.kms.region should be excluded as it will be ignored
            endpoint:
                # Required: service endpoint (vpc endpoint or standard regional endpoint); https://kms.eu-central-1.amazonaws.com is also valid
                service-endpoint: kms.eu-central-1.amazonaws.com
                
                # Optional: signing region for SigV4 signing of requests - if used, should be different from the already regional service-endpoint
                signing-region: us-east-1
                
                

The *aws.kms.keyId* property is required only if you intend to encrypt values in your application. The following contains the properties used by this library.

- aws.kms.keyId
    - either the keyId or the full ARN of the KMS key
- aws.kms.enabled (defaults to true)
- aws.kms.endpoint
    - if used, this will cause aws.kms.region to be ignored
- aws.kms.endpoint.service-endpoint
    - endpoint address with or without https:// prefix
- aws.kms.endpoint.signing-region 
    - in most cases can be omitted
    - if provided, it will usually differ from region that hosts the service-endpoint
 

**AWS region** and **credentials** are taken from the environment through the
[Default Credential Provider Chain](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default)
and the [Default Region Provider Chain](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment).
However, the region can be overwritten by property `aws.kms.region` if necessary.

Usage
-----

Now you can add encrypted values to you property files. An encrypted value must always start with `{cipher}`.
Those properties are automatically decrypted on application startup.

E.g. `application.yml`

    secretPassword: '{cipher}CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

### Use an encryption context

An [encryption context](http://docs.aws.amazon.com/kms/latest/developerguide/encryption-context.html)
is a set of key-value pairs used for encrypt and decrypt values, which might be useful as security
enhancement.

To use an encryption context with this library, you will have to use a custom syntax, that is not part
of Spring Security (as the {cipher} prefix).

E.g. `application.yml`

    secretPassword: '{cipher}(Country=UG9ydHVnYWw=,Code=MzUx)CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

The `(Country=UG9ydHVnYWw=,Code=MzUx)` part is the encryption context, where we used two keys for
this example: Country and Code. And the values are Base64 encoded.

Key-value pairs must be comma separated, and it is fine to use spaces to separate values. The order of the
values in the context is not important. And one last note, is that the values used in the encryption
context are logged in CloudTrail, so they must not be sensitive.

### Use extra options

While decrypting config values, extra arguments can be supplied to control the output behavior.
Extra args do also require a custom syntax, that is not part of Spring Security (as the {cipher} prefix).

E.g. `application.yml`

    secretKey: '{cipher}[output=base64]CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

The `[output=base64]` part defines the extra options.

Encryption context and extra options can be combined in any order.
`"{cipher}[output=base64](Code=MzUx)..."` is equivalent to `"{cipher}(Code=MzUx)[output=base64]..."`.

#### Available Options
| Option | Values | Default | Description |
| ------ | ------ | ------- | ----------- |
| output | `plain`, `base64` | `plain` | `plain` returns the decrypted secret as simple String. `base64` returns the decrypted secret in Base64 encoding. This is useful in cases where the plaintext secret contains non-printable characters (e.g. random AES keys) |


Hints
-----

### How to get the cipher text?

The Spring Cloud Config Server library provides an endpoint to encrypt plain text strings. Make sure to secure this endpoint properly!
See [reference](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_encryption_and_decryption) for details.

You can also use [AWS CLI](http://docs.aws.amazon.com/cli/latest/reference/kms/encrypt.html#examples) or our small
[CLI tool](https://github.com/zalando/spring-cloud-config-aws-kms/tree/master/encryption-cli) to encrypt and decrypt values.


Releases
--------

### Release to Maven Central

    mvn clean release:prepare -Dresume=false

    mvn release:perform

Contributing
------------

Contributions are highly welcome. For details please refer to the [guidelines](https://github.com/zalando/spring-cloud-config-aws-kms/tree/master/CONTRIBUTING.md).

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
