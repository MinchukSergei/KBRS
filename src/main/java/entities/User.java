package entities;

import java.util.Arrays;

public class User {
    private String userLogin;
    private byte[] userPassword;
    private byte[] userPubKey;
    private byte[] userDSPubKey;
    private byte[] userKSData;

    public byte[] getUserKSData() {
        return userKSData;
    }

    public void setUserKSData(byte[] userKSData) {
        this.userKSData = userKSData;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public byte[] getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(byte[] userPassword) {
        this.userPassword = userPassword;
    }

    public byte[] getUserDSPubKey() {
        return userDSPubKey;
    }

    public void setUserDSPubKey(byte[] userDSPubKey) {
        this.userDSPubKey = userDSPubKey;
    }

    public byte[] getUserPubKey() {
        return userPubKey;
    }

    public void setUserPubKey(byte[] userPubKey) {
        this.userPubKey = userPubKey;
    }


}
