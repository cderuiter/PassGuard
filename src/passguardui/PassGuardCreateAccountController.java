/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqlite.SQLiteHelper;
import sqlite.SQLitePassGuardLoginHelper;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.image.Image;
import static passguardui.PassGuardLogin.iconPath;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardCreateAccountController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField retypePasswordField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    
    public void start() throws Exception{
        Stage window = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PassGuardCreateAccountFXML.fxml"));
        Scene scene = new Scene(root);
        
        Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
        window.getIcons().add(icon);
        
        window.initModality(Modality.APPLICATION_MODAL); //makes sure the user will interact with this window
        window.setTitle("Create PassGuard Account");
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    private void handleCancelButton(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleCreateAccountButton(){
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String retypePassword = retypePasswordField.getText();
        
        if(!username.equals("") && !password.equals("") && !retypePassword.equals("")){
            if(SQLitePassGuardLoginHelper.validateNewUser(username)){
                if(password.equals(retypePassword)){
                    SQLiteHelper.SQLLiteDatabaseConnection();
                    SQLiteHelper.createNewTable();

                    SQLitePassGuardLoginHelper.createNewAccountTable();
                    SQLitePassGuardLoginHelper.insertLoginInfo(username, password);

                    Stage stage = (Stage) createAccountButton.getScene().getWindow();
                    stage.close();
                }
                else{
                    popUpError("Passwords are not matching!");
                }
            }
            else{
                popUpError("Username Already Exists");
            } 
        }
        else{
            popUpError("Fill In Any Missing Fields!");
        }
        
    }
    
    private void popUpError(String message){
        try{
            Stage window = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PopUpFXML.fxml").openStream());
            Scene scene = new Scene(root);
            PopUpController popupWindow = (PopUpController) loader.getController();
            popupWindow.setErrorLabel(message);
            Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
            window.getIcons().add(icon);
            window.setTitle("Error");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            window.show();
        }
        catch(IOException e){
            Platform.exit();
        }
    }
    
}
