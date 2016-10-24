package util;

/**
 * Created by USER on 24.10.2016.
 */
public class CredentialMessage {
    private String login;
    private byte[] password;
    private byte[] sign;

    public CredentialMessage(String login, byte[] password, byte[] sign) {
        this.login = login;
        this.password = password;
        this.sign = sign;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }
}
