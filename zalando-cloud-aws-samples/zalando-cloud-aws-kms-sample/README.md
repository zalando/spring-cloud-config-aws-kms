# Sample

Startup localstack with KMS

```bash
docker run -d --name localstack -p 4566:4566 -p 4510-4559:4510-4559 localstack/localstack
```

Create customer-managed key and note down KeyId from the AWS CLI output

```bash
aws --endpoint-url http://localhost:4566 kms create-key
```

Encode secret using base64

```bash
echo "secret" | base64
```

Encrypt secret using AWS CLI, note the `CiphertextBlob` from the AWS CLI output

```bash
aws --endpoint-url http://localhost:4566 kms encrypt --key-id <key-id> --plaintext <base-64 encoded secret>
```

Configure the sample [application.yaml](./src/main/resources/application.yaml) to
add the CiphertextBlob value from the previous.

```yaml
sample:
  password: '{cipher}<CiphertextBlob>'
  
```

Run the sample spring boot application

```bash
 cd ./zalando-cloud-aws-samples/zalando-cloud-aws-kms-sample
 ../../mvnw spring-boot:run
```

The application will print out confirmation that the secret was decrypted to the console
```
2024-03-14T10:09:23.638+01:00[0;39m [32m INFO[0;39m [35m1174881[0;39m [2m---[0;39m [2m[           main][0;39m [2m[0;39m[36mc.z.a.c.b.encrypt.KmsTextEncryptor      [0;39m [2m:[0;39m decrypting NGJlZDYyNzEtNjRhOS00OTRhLWJhMGItZjk2MmIyMmIyYWM1rHhX2gJ7okYuE5VvxT0mN0ZkatF+b3AdmNGYdj21/hfd5oFm3DBaTvHHPJppbXTX as part of stack.
[java.base/java.lang.Thread.getStackTrace(Thread.java:1610), 
  com.zalando.awsspring.cloud.bootstrap.encrypt.KmsTextEncryptor.decrypt(KmsTextEncryptor.java:70), 
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:143), 
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.lambda$decrypt$0(AbstractEnvironmentDecrypt.java:136),
  java.base/java.util.LinkedHashMap.replaceAll(LinkedHashMap.java:731),
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:131), 
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:70), 
  org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer.initialize(EnvironmentDecryptApplicationInitializer.java:95), 
  org.springframework.cloud.bootstrap.BootstrapApplicationListener$DelegatingEnvironmentDecryptApplicationInitializer.initialize(BootstrapApplicationListener.java:414), 
  org.springframework.boot.SpringApplication.applyInitializers(SpringApplication.java:627),
  org.springframework.boot.SpringApplication.prepareContext(SpringApplication.java:400),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:333),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:1354),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:1343),
  com.zalando.awsspring.samples.bootstrap.BootstrapApplication.main(BootstrapApplication.java:10)]
[2m2024-03-14T10:09:23.801+01:00[0;39m [32m INFO[0;39m [35m1174881[0;39m [2m---[0;39m [2m[           main][0;39m [2m[0;39m[36mc.z.a.c.b.encrypt.KmsTextEncryptor      [0;39m [2m:[0;39m decrypting NGJlZDYyNzEtNjRhOS00OTRhLWJhMGItZjk2MmIyMmIyYWM1rHhX2gJ7okYuE5VvxT0mN0ZkatF+b3AdmNGYdj21/hfd5oFm3DBaTvHHPJppbXTX as part of stack.
[java.base/java.lang.Thread.getStackTrace(Thread.java:1610),
  com.zalando.awsspring.cloud.bootstrap.encrypt.KmsTextEncryptor.decrypt(KmsTextEncryptor.java:70),
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:143),
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.lambda$decrypt$0(AbstractEnvironmentDecrypt.java:136),
  java.base/java.util.LinkedHashMap.replaceAll(LinkedHashMap.java:731),
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:131),
  org.springframework.cloud.bootstrap.encrypt.AbstractEnvironmentDecrypt.decrypt(AbstractEnvironmentDecrypt.java:70),
  org.springframework.cloud.bootstrap.encrypt.EnvironmentDecryptApplicationInitializer.initialize(EnvironmentDecryptApplicationInitializer.java:95),
  org.springframework.boot.SpringApplication.applyInitializers(SpringApplication.java:627),
  org.springframework.boot.SpringApplication.prepareContext(SpringApplication.java:400),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:333),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:1354),
  org.springframework.boot.SpringApplication.run(SpringApplication.java:1343),
  com.zalando.awsspring.samples.bootstrap.BootstrapApplication.main(BootstrapApplication.java:10)]
```

