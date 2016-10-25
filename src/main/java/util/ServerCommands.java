package util;

/**
 * Created by USER on 24.09.2016.
 */
public enum  ServerCommands {
    SEND_ENCODED_SESSION_KEY(0),
    SEND_ENCODED_FILE(1),
    SEND_EOF(2),
    SEND_SESSION_TOKEN(3),

    // FILE SENDING CONSTANTS
    FILE_EXISTS(10),
    FILE_NOT_FOUND(11),
    END_OF_FILE(13),
    SESSION_KEY_IS_NULL(21),
    PUBLIC_KEY_IS_NULL(23),
    PUBLIC_KEY_IS_CORRECT(24),

    // CONStANTS
    SERVER_PART_FILE_LENGTH(128),
    SERVER_PART_FILE_ENC_LENGTH(144),
    SESSION_ENC_KEY_BYTE_LENGTH(256),

    //CREDENTIALS
    CORRECT_SIGN(51),
    INCORRECT_SIGN(52),
    CORRECT_CREDENTIALS(53),
    INCORRECT_CREDENTIALS(54),
    INCORRECT_TOKEN(55),
    CORRECT_TOKEN(56);

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

    public static ServerCommands getCommandByValue(int value) {
        for (ServerCommands c: ServerCommands.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }
}
