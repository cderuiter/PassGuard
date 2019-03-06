import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PassGuardStartupUI extends Application{
	
	private final int windowWidth = 350, windowHeight = 300;
	private Stage window;
    private Button loginButton, createAccountButton;
	
//	public static void main(String[] args) {
//		launch(args);
//	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
        window.setTitle("PassGuard Login");
        
        //Labels
        Label welcomeLabel = new Label("Welcome to PassGuard!");
        welcomeLabel.setFont(new Font("Arial", 24));
        Label credentialsLabel = new Label("If you are an existing user, please enter your credentials below");
        Label newAccountLabel = new Label("If you are a new user, please press 'Create New Account'");
        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");
        
        //Textfield Inputs
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username here...");
        TextField passwordInput = new TextField();
        passwordInput.setPromptText("Password here...");
        
        //Buttons
        loginButton = new Button("Log In");
        //loginButton.setOnAction();
        createAccountButton = new Button("Create New Account");
        
        //Button Functionality
        loginButton.setOnAction(e -> {
        	String username = usernameInput.getText();
        	String password = passwordInput.getText();
        	
        	//need to check username and password from database
        	
        	if(true) { //if credentials are true, then log in
        		PassGuardMainUI.start();
        		window.close();
        	}
        });
        
        //Setting up Layout
        GridPane gridLayout = new GridPane();
        GridPane.setConstraints(usernameLabel, 0, 0);
        GridPane.setConstraints(usernameInput, 1, 0);
        GridPane.setConstraints(passwordLabel, 0, 1);
        GridPane.setConstraints(passwordInput, 1, 1);
        GridPane.setConstraints(loginButton, 1, 2);
        GridPane.setConstraints(createAccountButton, 1, 3);
        
        gridLayout.setPadding(new Insets(10, 10, 10, 10));
        gridLayout.setVgap(8);
        gridLayout.setHgap(10);
        gridLayout.setAlignment(Pos.CENTER);
        gridLayout.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, 
        		passwordInput, loginButton, createAccountButton);
        
        //VBox
        VBox vboxLayout = new VBox();
        vboxLayout.setPadding(new Insets(10, 10, 10, 10));
        vboxLayout.getChildren().addAll(welcomeLabel, credentialsLabel, newAccountLabel);
        
        //BorderPane
        BorderPane borderPaneLayout = new BorderPane();
        borderPaneLayout.setCenter(gridLayout);
        borderPaneLayout.setTop(vboxLayout);
        
        //Setting up scene and showing window
        Scene scene = new Scene(borderPaneLayout, windowWidth, windowHeight);
        window.setScene(scene);
        window.show();
		
	}

}
