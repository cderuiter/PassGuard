
package passguardui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.stage.Modality;
import javafx.stage.Stage;


public class PassGuardLoginController implements Initializable {

    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    
    @FXML
    private void handleLogInButton(ActionEvent event){
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        
        if(true){//if the username and password are correct, then go to main UI
            PassGuardMainController mainUI = new PassGuardMainController();
            try{
              mainUI.start();
              Stage stage = (Stage) loginButton.getScene().getWindow();
              stage.close();
            }
            catch(Exception e){
                Platform.exit();
            }  
        }
        else{//if the credentials are not correct, give incorrect username/password prompt
            popUpError("Incorrect Credentials!");
        }
    }
    
    @FXML
    private void handleCreateAccountButton(ActionEvent event){
        PassGuardCreateAccountController createAccount = new PassGuardCreateAccountController();
        try{
            createAccount.start();
        }
        catch(Exception e){
            Platform.exit();
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
