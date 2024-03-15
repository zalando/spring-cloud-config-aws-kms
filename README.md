# Zalando Cloud AWS

[![Build Status](https://travis-ci.org/zalando/spring-cloud-config-aws-kms.svg?branch=master)](https://travis-ci.org/zalando/spring-cloud-config-aws-kms)
[![Javadocs](http://javadoc.io/badge/org.zalando/spring-cloud-config-aws-kms.svg?color=blue)](http://javadoc.io/doc/org.zalando/spring-cloud-config-aws-kms)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/spring-cloud-config-aws-kms.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/spring-cloud-config-aws-kms)
[![License](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](https://raw.githubusercontent.com/zalando-incubator/aws-support-spring-boot-starter/master/LICENSE)

Zalando Cloud AWS is an addition to [Spring Cloud AWS](https://github.com/awspring/spring-cloud-aws) that simplifies using AWS managed services in a Spring and Spring boot application.

## Compatibility with Spring Project Versions

This project has dependency and transitive dependency on Spring Projects. The table below outlines the version of Spring Cloud AWS, Spring Cloud, Spring Boot and Spring Framework versions that are compatible with certain Zalando Cloud AWS versions.

| Zalando Cloud AWS | Spring Cloud AWS | Spring Boot | Spring Framework | AWS Java SDK |
| ----------------- | ---------------- | ----------- | ---------------- | ------------ |
| 3.1.x             | 3.1.x            | 3.2.x       | 6.1.x            | 2.x          |

## Supported AWS integrations

| AWS Service | Zalando Cloud AWS 3.x | 
| ----------- | --------------------- |
| KMS         | ✅                    |

Note that Spring Cloud AWS and Spring Cloud provide support for other AWS services in their projects.


Development
-----------

### Run Test Suite

    mvn clean test
    
### Coverage Report

    open coverage/target/site/jacoco/index.html

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
