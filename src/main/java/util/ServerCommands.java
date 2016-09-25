package util;

/**
 * Created by USER on 24.09.2016.
 */
public enum  ServerCommands {
    SEND_ENCODED_SESSION_KEY(0),
    SEND_ENCODED_FILE(1),
    SEND_EOF(2),

    // FILE SENDING CONSTANTS
    FILE_EXISTS(10),
    FILE_NOT_FOUND(11),
    END_OF_FILE(13),
    SESSION_KEY_IS_NULL(21),

    // CONStANTS
    SERVER_PART_FILE_LENGTH(128),
    SERVER_PART_FILE_ENC_LENGTH(144),
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
