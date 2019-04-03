/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author yusuf
 */
public class PassGuardLogin extends Application {
    
    public final static String iconPath = "/res/shield.png";
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception{
        Stage window = stage;
        Parent root = FXMLLoader.load(getClass().getResource("PassGuardLoginFXML.fxml"));
        
        Scene scene = new Scene(root);
        
        Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
        window.getIcons().add(icon);
        
        window.setTitle("PassGuard Log-In");
        window.setScene(scene);
        window.show();
    }

    
}
