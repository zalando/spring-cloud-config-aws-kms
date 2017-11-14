package de.zalando.spring.cloud.config.aws.kms.test;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import de.zalando.spring.cloud.config.aws.kms.KmsTextEncryptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class EncryptionCLI implements CommandLineRunner {

    @Override
    public void run(final String... args) {
        try {
            checkArgument(args.length >= 2, "Too few arguments.");

            final String text = args[1];
            final AWSKMS kms = AWSKMSClientBuilder.defaultClient();

            switch (args[0]) {

                case "encrypt" :
                    checkArgument(args.length == 3, "Too few arguments.");
                    System.out.println(new KmsTextEncryptor(kms, args[2]).encrypt(text));
                    break;

                case "decrypt" :
                    System.out.println(new KmsTextEncryptor(kms, null).decrypt(text));
                    break;

                default :

                    break;
            }
        } catch (final IllegalArgumentException e) {
            System.out.println(e.getMessage() + " Usage:\n"        //
                    + "Make sure a region is set, either in ~/.aws/config " //
                    + "or with `export AWS_REGION=eu-central-1`\n" //
                    + "\n" //
                    + "then do\n" //
                    + "./run.sh encrypt 'plaintext' ${kmsKeyId}\n" //
                    + "./run.sh decrypt 'base64cipherText'");
        }
    }
}
