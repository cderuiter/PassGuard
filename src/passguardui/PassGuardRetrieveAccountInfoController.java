/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
//import javafx.scene.input.Clipboard;
//import javafx.scene.input.ClipboardContent;


import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardRetrieveAccountInfoController{

    @FXML
    private TextField accountNameText;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextArea notesText;
    @FXML
    private Button okButton;
    @FXML
    private Button copyButton;
    
    
    
    public void setInfo(String account, String username, String password, String notes) {       
        accountNameText.setText(account);
        accountNameText.setEditable(false);
        accountNameText.setMouseTransparent(true);
        usernameText.setText(username);
        usernameText.setEditable(false);
        usernameText.setMouseTransparent(true);
        passwordText.setText(password);
        passwordText.setEditable(false);
        passwordText.setMouseTransparent(true);
        notesText.setText(notes);
        notesText.setEditable(false);
    }   
    
    @FXML
    private void handleCopyButton(){

        StringSelection selection = new StringSelection(passwordText.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        ClipboardWaitThread waitThread = new ClipboardWaitThread();
        waitThread.start();
        
    }
    
    
    
    @FXML
    private void handleOKButton(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
    
}
