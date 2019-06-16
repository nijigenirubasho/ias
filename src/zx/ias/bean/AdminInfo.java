package zx.ias.bean;

import java.io.Serializable;

/**
 * AdminPOJO
 */

public class AdminInfo implements Serializable {

    //固定serialVersionUID保证(反)序列化正常
    private static final long serialVersionUID = 1L;

    private String userName, password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
