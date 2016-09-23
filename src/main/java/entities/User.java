package entities;

public class User {
    private String userLogin;
    private String userPassword;
    private String userPrKey;
    private String userEmail;
    private String userSessionKey;

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

    public String getUserPrKey() {
        return userPrKey;
    }

    public void setUserPrKey(String userPrKey) {
        this.userPrKey = userPrKey;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserSessionKey() {
        return userSessionKey;
    }

    public void setUserSessionKey(String userSessionKey) {
        this.userSessionKey = userSessionKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userLogin != null ? !userLogin.equals(user.userLogin) : user.userLogin != null) return false;
        if (userPassword != null ? !userPassword.equals(user.userPassword) : user.userPassword != null) return false;
        if (userPrKey != null ? !userPrKey.equals(user.userPrKey) : user.userPrKey != null) return false;
        if (userEmail != null ? !userEmail.equals(user.userEmail) : user.userEmail != null) return false;
        return userSessionKey != null ? userSessionKey.equals(user.userSessionKey) : user.userSessionKey == null;

    }

    @Override
    public int hashCode() {
        int result = userLogin != null ? userLogin.hashCode() : 0;
        result = 31 * result + (userPassword != null ? userPassword.hashCode() : 0);
        result = 31 * result + (userPrKey != null ? userPrKey.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (userSessionKey != null ? userSessionKey.hashCode() : 0);
        return result;
    }
}
