# Zalando Cloud AWS KMS

This is a Spring Cloud AWS add-on that provides a KMS client and encryption via AWS (Amazon Web Services) KMS (Key management service).

## Features

* Compatible with Spring Cloud and Spring Cloud AWS
* Spring Boot 3 - ready
* Supports AWS KMS [encryption context](#use-an-encryption-context)
* Supports different [output modes](#available-options) for decrypted values
* Supports [asymmetric keys](#asymmetric-keys)
* Minimal dependencies

## Installation

### Prerequisites

Given you have a [Spring Boot](http://projects.spring.io/spring-boot/) application.

### Step 1

#### Add a property to `<properties>`.

```xml
<properties>
    ...
    <zalando-cloud-aws.version>3.1.1</zalando-cloud-aws.version>
</properties>
```

#### Add the starter as dependency to your Maven `pom.xml` or Gradle build file.

```xml
<dependency>
    <groupId>org.zalando.awspring.cloud</groupId>
    <artifactId>zalando-cloud-aws-starter-kms</artifactId>
    <version>${zalando-cloud-aws.version}</version>
</dependency>
```

### Step 2 (optional)

Apply configuration to the application's [Bootstrap Context](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/application-context-services.html#the-bootstrap-application-context), e.g., `bootstrap.yml`:

```yaml
spring:
  cloud:
    decrypt-environment-post-processor:
      enabled: false # disable environment post processor for rsa keys
---
spring:
  cloud:
    aws:
      region:
        static: eu-central-1 # optional
      kms:
        endpoint: http://localhost:4566 # only needed for endpoint override

encrypt:
  kms:
    # Optional: Turn off the KMS feature completely (e.g. for local development) 
    enabled: false

    # Optional for decrypting values with SYMMETRIC_DEFAULT algorithm.
    # Required for encrypting values.
    # Required for decrypting values with some asymmetric algorithm. 
    key-id: 9d9fca31-54c5-4df5-ba4f-127dfb9a5031

    # Optional: Switch to asymmetric algorithm.
    # See com.amazonaws.services.kms.model.EncryptionAlgorithmSpec for available values.
    encryption-algorithm: "RSAES_OAEP_SHA_256"
```

The `spring.cloud.aws.kms.key-id` property must be set if
- values need to be decrypted with an asymmetric key
- values need to be encrypted (with any algorithm)

Those are the properties used by this library:

- `encrypt.kms.enabled`: (defaults to true)
- `encrypt.kms.key-id`: either the keyId or the full ARN of the KMS key
- `encrypt.kms.encryption-algorithm`: the encryption algorithm to use


## Usage

Now you can add encrypted values to you property files. An encrypted value must always start with `{cipher}`.
Those properties are automatically decrypted on application startup, e.g., `application.yml`

```yaml
    secretPassword: '{cipher}CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='
```
### Use an encryption context

An [encryption context](http://docs.aws.amazon.com/kms/latest/developerguide/encryption-context.html)
is a set of key-value pairs used for encrypt and decrypt values, which might be useful as security
enhancement.

To use an encryption context with this library, you will have to use a custom syntax, that is not part
of Spring Security (as the `{cipher}` prefix), e.g., `application.yml`

```yaml
    secretPassword: '{cipher}(Country=UG9ydHVnYWw=,Code=MzUx)CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='
```
The `(Country=UG9ydHVnYWw=,Code=MzUx)` part is the encryption context, where we used two keys for
this example: Country and Code. The values are Base64 encoded.

Key-value pairs must be comma separated, and it is fine to use spaces to separate values. The order of the
values in the context is not important. And one last note, is that the values used in the encryption
context are logged in CloudTrail, so they must not be sensitive.

### Asymmetric keys

AWS KMS supports [symmetric and asymmetric keys](https://docs.aws.amazon.com/kms/latest/developerguide/symmetric-asymmetric.html) to encrypt/decrypt data. By default, this library assumes a symmetric key. There are configuration options available to enable asymmetric keys.

#### Encryption

Add `key-id` and `encryption-algorithm` to the `bootstrap.yml`:

```yaml
encrypt:
  kms:
    key-id: "9d9fca31-54c5-4df5-ba4f-127dfb9a5031"
    encryption-algorithm: "RSAES_OAEP_SHA_256"  # or "RSAES_OAEP_SHA_1"
```


#### Decryption

If all cipher values of your application have been encrypted with the same KMS key and algorithm, you can configure
the `keyId` and `encryptionAlgorithm` globally in the `bootstrap.yml` as shown above. In case you have to decrypt
ciphers from different keys or different algorithms, you can specify those separately for each key using the
["extra options"](#use-extra-options) approach, e.g., `application.yml`

```yaml
    secret1: "{cipher}SSdtIHNvbWUgYXN5bW1ldHJpY2FsbHkgZW5jcnlwdGVkIHNlY3JldA=="
    secret2: "{cipher}[encryptionAlgorithm=SYMMETRIC_DEFAULT]U3ltbWV0cmljIGFuZCBhc3ltbWV0cmljIHNlY3JldHMgY2FuIGJlIG1peGVk"
    secret3: "{cipher}[encryptionAlgorithm=RSAES_OAEP_SHA_256,keyId=9d9fca31-54c5-4df5-ba4f-127dfb9a5031]SSBoYXZlIGEgY3VzdG9tIGtleSBhbmQgYWxnb3JpdGht"
```
### Use extra options

While decrypting config values, extra arguments can be supplied to control the output behavior.
Extra args do also require a custom syntax, that is not part of Spring Security (as the `{cipher}` prefix), e.g., `application.yml`

```yaml
    secretKey: '{cipher}[output=base64]CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='
```

The `[output=base64]` part defines the extra options.

Encryption context and extra options can be combined in any order.
`"{cipher}[output=base64](Code=MzUx)..."` is equivalent to `"{cipher}(Code=MzUx)[output=base64]..."`.

#### Available Options

| Option | Values | Default | Description |
| ------ | ------ | ------- | ----------- |
| output | `plain`, `base64` | `plain` | `plain` returns the decrypted secret as simple String. `base64` returns the decrypted secret in Base64 encoding. This is useful in cases where the plaintext secret contains non-printable characters (e.g. random AES keys) |
| encryptionAlgorithm | as defined in `software.amazon.awssdk.services.kms.model.EncryptionAlgorithmSpec` | `null` | Use the algorithm to decrypt the cipher text. |
| keyId | ID or full ARN of a KMS key | `null` | Use the given key to decrypt the cipher text |


## Development

### Run Test Suite

```shell
mvn clean test
```

## Releases

### Release to Maven Central

```shell
mvn clean release:prepare -Dresume=false
mvn release:perform
```

## Contributing

Contributions are highly welcome. For details please refer to the [guidelines](https://github.com/zalando/spring-cloud-config-aws-kms/tree/master/CONTRIBUTING.md).

## License

Copyright (C) 2024 Zalando SE (https://tech.zalando.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.