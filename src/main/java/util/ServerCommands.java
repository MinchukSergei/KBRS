package util;

/**
 * Created by USER on 24.09.2016.
 */
public enum  ServerCommands {
    SEND_ENCODED_SESSION_KEY(0),
    SEND_ENCODED_FILE(1),
    SEND_EOF(2),
    SESSION_ENC_KEY_BYTE_LENGTH(256);

    ServerCommands(int value) {
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
