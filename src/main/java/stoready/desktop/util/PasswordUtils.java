package stoready.desktop.util;

import org.apache.commons.codec.digest.Sha2Crypt;

public final class PasswordUtils {

    private PasswordUtils() {
    }

    public static String encrypt(String plain) {
        return Sha2Crypt.sha256Crypt(plain.getBytes());
    }

    public static boolean isValid(String plain, String encrypted) {
        return encrypted.equals(Sha2Crypt.sha256Crypt(plain.getBytes(), encrypted));
    }

}
