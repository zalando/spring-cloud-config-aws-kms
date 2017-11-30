Installation
------------

    mvn clean package

Preparation
-----------

Configure AWS [credentials](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default)
and [region](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html#automatically-determine-the-aws-region-from-the-environment).

Usage
-----

### Encrypt

    $ ./run.sh --encrypt-plaintext='Hello World!' --aws.kms.keyId='9d9fca31-54c5-4df5-ba4f-127dfb9a5031'

### Decrypt

    $ ./run.sh --decrypt.cipherText='CiA47hYvQqWFFGq3TLtzQO5ArcwDkjq69Q=='
