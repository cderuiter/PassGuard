/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import sqlite.SQLiteHelper;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardRetrieveAccountInfoController{

    @FXML
    private Label accountNameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label notesLabel;
    @FXML
    private Button okButton;
    @FXML
    private Button copyButton;
    
    public void setInfo(String account, String username, String password, String notes) {       
        accountNameLabel.setText(account);
        usernameLabel.setText(username);
        passwordLabel.setText(password);
        notesLabel.setText(notes);
    }   
    
    @FXML
    private void handleCopyButton(){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(passwordLabel.getText());
        clipboard.setContent(content);
    }
    
    @FXML
    private void handleOKButton(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
    
}
