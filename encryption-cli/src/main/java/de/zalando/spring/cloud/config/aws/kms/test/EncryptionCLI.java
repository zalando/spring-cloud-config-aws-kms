package de.zalando.spring.cloud.config.aws.kms.test;

import de.zalando.spring.cloud.config.aws.kms.KmsTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
public class EncryptionCLI implements CommandLineRunner {

    private final KmsTextEncryptor kmsTextEncryptor;
    private final EncryptProperties encrypt;
    private final DecryptProperties decrypt;

    @Autowired
    public EncryptionCLI(KmsTextEncryptor kmsTextEncryptor, EncryptProperties encrypt, DecryptProperties decrypt) {
        this.kmsTextEncryptor = kmsTextEncryptor;
        this.encrypt = encrypt;
        this.decrypt = decrypt;
    }

    private void printUsage() {
        System.out.println("Usage:\n"        //
                + "Make sure that AWS credentials and region are set, either in ~/.aws/config, ~/.aws/credentials\n" //
                + "or via environment variables, e.g. `export AWS_REGION=eu-central-1`\n" //
                + "\n" //
                + "then do\n" //
                + "./run.sh --encrypt-plaintext='Hello World!' --aws.kms.keyId='9d9fca31-54c5-4df5-ba4f-127dfb9a5031'\n" //
                + "./run.sh --decrypt.cipherText='CiA47hYvQqWFFGq3TLtzQO5ArcwDkjq69Q=='");
    }

    @Override
    public void run(final String... args) {
        final String plaintext = encrypt.getPlaintext();
        final String cipherText = decrypt.getCipherText();
        try {
            if (hasText(plaintext)) {
                System.out.println(kmsTextEncryptor.encrypt(plaintext));
            } else if (hasText(cipherText)) {
                System.out.println(kmsTextEncryptor.decrypt(cipherText));
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printUsage();
        }
    }
}
