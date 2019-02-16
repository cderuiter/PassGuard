/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author yusuf
 */
public class PassGuardLogin extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception{
        Stage window = stage;
        Parent root = FXMLLoader.load(getClass().getResource("PassGuardLoginFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        window.setTitle("PassGuard Log-In");
        window.setScene(scene);
        window.show();
    }

    
}
