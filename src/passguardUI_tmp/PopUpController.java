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
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PopUpController {
    
    @FXML
    private Button okButton;
    @FXML
    private Label errorLabel;
    
    public void setErrorLabel(String errorMessage){
        errorLabel.setText(errorMessage);
    }
    
    @FXML
    private void handleOKButton(){
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
    
}
