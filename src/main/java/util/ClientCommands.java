package util;

/**
 * Created by USER on 24.09.2016.
 */
public enum ClientCommands {
    SEND_PUBLIC_RSA_KEY(0),
    SEND_FILENAME(1),

    //FILE READING
    CORRECT_FILE_RECEIVING(10),
    ERROR_FILE_RECEIVING(11),
    //CONSTANT
    RSA_PUBLIC_KEY_BYTE_LENGTH(294);

    ClientCommands(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
