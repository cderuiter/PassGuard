/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardUI;

/**
 *
 * @author yusuf
 */
public class AccountInfo {
    
    private String account;
    private String username;
    private String password;
    private String notes;

    public AccountInfo(String account, String username, String password, String notes) {
        this.account = account;
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
}
