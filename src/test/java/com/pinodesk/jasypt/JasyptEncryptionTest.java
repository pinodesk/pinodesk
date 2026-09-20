package com.pinodesk.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptEncryptionTest {

    @Test
    public void testEncryptionDecryption() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHMD5ANDTRIPLEDES");
        encryptor.setPassword("R6KgG8InZunDMvVRpSYTixZP");
        String encrypted = encryptor.encrypt("test");
        System.out.println(encrypted);
        String decrypted = encryptor.decrypt(encrypted);
        System.out.println(decrypted);
    }

}
