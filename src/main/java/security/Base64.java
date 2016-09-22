package security;

/**
 * Created by USER on 22.09.2016.
 */
public class Base64 {
    public static boolean isBase64(String string) {
        return org.apache.commons.codec.binary.Base64.isBase64(string);
    }

    public static String encodeToBase64(byte[] message) {
        return java.util.Base64.getEncoder().encodeToString(message);
    }

    public static byte[] decodeFromBase64(String message) {
        return java.util.Base64.getDecoder().decode(message);
    }
}
