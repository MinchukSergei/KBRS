package entities;

import java.util.Arrays;

public class User {
    private String userLogin;
    private String userPassword;
    private byte[] userPrKey;
    private String userEmail;
    private byte[] userSessionKey;
    private byte[] userPubKey;

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public byte[] getUserPrKey() {
        return userPrKey;
    }

    public void setUserPrKey(byte[] userPrKey) {
        this.userPrKey = userPrKey;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public byte[] getUserSessionKey() {
        return userSessionKey;
    }

    public void setUserSessionKey(byte[] userSessionKey) {
        this.userSessionKey = userSessionKey;
    }

    public byte[] getUserPubKey() {
        return userPubKey;
    }

    public void setUserPubKey(byte[] userPubKey) {
        this.userPubKey = userPubKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userLogin != null ? !userLogin.equals(user.userLogin) : user.userLogin != null) return false;
        if (userPassword != null ? !userPassword.equals(user.userPassword) : user.userPassword != null) return false;
        if (!Arrays.equals(userPrKey, user.userPrKey)) return false;
        if (userEmail != null ? !userEmail.equals(user.userEmail) : user.userEmail != null) return false;
        if (!Arrays.equals(userSessionKey, user.userSessionKey)) return false;
        return Arrays.equals(userPubKey, user.userPubKey);

    }

    @Override
    public int hashCode() {
        int result = userLogin != null ? userLogin.hashCode() : 0;
        result = 31 * result + (userPassword != null ? userPassword.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(userPrKey);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(userSessionKey);
        result = 31 * result + Arrays.hashCode(userPubKey);
        return result;
    }
}
