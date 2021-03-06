
package passguardui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import crypto.StreamHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static passguardui.PassGuardLogin.iconPath;
import sqlite.SQLitePassGuardLoginHelper;


public class PassGuardLoginController implements Initializable {
	
	public static String currentUserID;

    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    
    @FXML
    private void handleLogInButton(){
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        
        if(validateLogin(username, password)){ //if the username and password are correct, then go to main UI
            PassGuardMainController mainUI = new PassGuardMainController();
            try {
                currentUserID = StreamHelper.digest(SQLitePassGuardLoginHelper.getEncryptedPassword(username));
                mainUI.start();
		Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
            }
            catch(Exception e){
                
            }  
        }
        else{//if the credentials are not correct, give incorrect username/password prompt
            popUpError("Incorrect Credentials!");
        }
    }
    
    @FXML
    private void handleCreateAccountButton(){
        PassGuardCreateAccountController createAccount = new PassGuardCreateAccountController();
        try{
            createAccount.start();
        }
        catch(Exception e){
            
        }
    }
    
    @FXML
    private void handleEnterPress(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            handleLogInButton();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { //use this function if there is anything to load automatically upon startup
        
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
            window.resizableProperty().setValue(false); //makes it so you can not maximize
            window.setTitle("Error");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            window.show();
        }
        catch(IOException e){
            
        }
    }
    
    /**
     * @author cderuiter3
     *
     * This method accepts a username and password attempt,
     * searches the sqllite db for the login credentials
     * 
     * @param username - attempted username
     * @param password - attempted password
     * 
     * @return true if the username is in the database and the attempted password matches
     * else false
     */
    
    private boolean validateLogin(String username, String password) {
    	ArrayList<String> Usernames = SQLitePassGuardLoginHelper.getAllLoginUserNames();
    	if(Usernames.contains(StreamHelper.digest(username)) && SQLitePassGuardLoginHelper.getPassword(username).equals(password)) {
            return true;
    	}
    	else{
            return false;
    	}
    	
    }
    
    /**
     * @author cderuiter3
     *
     * This method sets the currentUserID
     * 
     * @param UserID - the current user's UserID/Primary Key
     * 
     */
    
    public static void setCurrentUser(String UserID) {
    	currentUserID = UserID;	
    }
    
    /**
     * @author cderuiter3
     *
     * This method gets the currentUserID
     * 
     * @return currentUserID - the current user's UserID/Primary Key
     * 
     */
    public static String getCurrentUser() {
    	return currentUserID;
    	
    }
    
}
