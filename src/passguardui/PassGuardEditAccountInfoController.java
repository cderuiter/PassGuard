/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sqlite.SQLiteHelper;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardEditAccountInfoController implements Initializable {

    
    private static String currentAccountName;
    private static String currentUsername;
    private static String currentPassword;
    private static String currentNotes;
    
    @FXML
    private TextField accountNameText;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextArea notesText;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    
    public void setCurrentInfo(String account, String username, String password, String notes){
        currentAccountName = account;
        currentUsername = username;
        currentPassword = password;
        currentNotes = notes;
        
        accountNameText.setText(account);
        accountNameText.setEditable(false);
        accountNameText.setMouseTransparent(true);
        usernameText.setText(username);
        passwordText.setText(password);
        notesText.setText(notes);
    }
    
    @FXML
    private void handleUpdateButton(){
        String newUsername = usernameText.getText();
        String newPassword = passwordText.getText();
        String newNotes = notesText.getText();
        SQLiteHelper.SQLLiteDatabaseConnection();
        
        if(!currentUsername.equals(newUsername)){
            SQLiteHelper.updateUsername(currentAccountName, newUsername);
        }
        if(!currentPassword.equals(newPassword)){
            SQLiteHelper.updatePassword(currentAccountName, newPassword);
        }
        if(!currentNotes.equals(newNotes)){
            SQLiteHelper.updateNotes(currentAccountName, newNotes);
        }
        
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCancelButton(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
