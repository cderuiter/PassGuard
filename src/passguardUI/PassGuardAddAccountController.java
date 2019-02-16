/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardUI;

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
import SQLite.SQLiteHelper;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardAddAccountController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button generatePasswordButton;
    @FXML
    private TextField accountTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField retypePasswordTextField;
    @FXML
    private TextArea notesTextArea;
    
    
    public void start() throws Exception{
        Stage window = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PassGuardAddAccountFXML.fxml"));
        Scene scene = new Scene(root);
        
        
        
        window.initModality(Modality.APPLICATION_MODAL); //makes sure the user will interact with this window
        window.setTitle("PassGuard: Add Account");
        window.setScene(scene);
        window.show();
    }
    
    @FXML
    private void handleAddButton(){
        String AccountName = accountTextField.getText();
        String UserName = usernameTextField.getText();
        String Password = passwordTextField.getText();
        String retypePassword = retypePasswordTextField.getText();
        String Notes = notesTextArea.getText();
        
        
        if(!AccountName.equals("") && !UserName.equals("") &&
            !Password.equals("") && !retypePassword.equals("") && !Notes.equals(""))
        {
            if(Password.equals(retypePassword)){
                SQLiteHelper.SQLLiteDatabaseConnection();
                SQLiteHelper.insertInfo(AccountName, UserName, Password, Notes);
                
                PassGuardMainController.getAllAccounts().add(new AccountInfo(AccountName, "**********", "**********", "**********"));
                
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.close();
            }
            else{
                //passwords not matching
                popUpError("Passwords Do Not Match!");
            }
        }
        else{
            //tell user to fill in any missing fields
            popUpError("Fill In Any Missing Fields!");
        }
    }
    
    @FXML
    private void handleCloseButton(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleGeneratePasswordButton(){
        
    }
    
    private void popUpError(String message){
        try{
            Stage window = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PopUpFXML.fxml").openStream());
            Scene scene = new Scene(root);
            PopUpController popupWindow = (PopUpController) loader.getController();
            popupWindow.setErrorLabel(message);
            window.setTitle("Error");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            window.show();
        }
        catch(IOException e){
            Platform.exit();
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
