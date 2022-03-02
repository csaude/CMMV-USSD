package mz.org.fgh.vmmc.model;

import java.io.Serializable;

public class LoginRequest  implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -8976016598331662988L;
    private String username;
    private String password;
    
    
    public LoginRequest() {
	super();
    }
    public LoginRequest(String username, String password) {
	super();
	this.username = username;
	this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
    

}
