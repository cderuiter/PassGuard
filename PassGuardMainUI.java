import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PassGuardMainUI{

	static final private int windowWidth = 600, windowHeight = 500;
	static private TableView<String> myTable;
	static private TableColumn<String,String> titleColumn;
	static private TableColumn<String,String> usernameColumn;
	static private TableColumn<String,String> passwordColumn;
	static private MenuBar menuBar = new MenuBar();
	static private Menu fileMenu, editMenu, viewMenu, helpMenu;

	
	public static void start(){
		Stage window = new Stage();
		window.setTitle("PassGuard");
		
		//Generate MenuBar
		generateTopMenu();
		
		//Generate Table
		generateTable();
		
		//Buttons
		
		
		//BorderPane Layout
		BorderPane borderPaneLayout = new BorderPane();
		borderPaneLayout.setRight(myTable);
		borderPaneLayout.setTop(menuBar);
		
		//Scene
		Scene scene = new Scene(borderPaneLayout, windowWidth, windowHeight);
		window.setScene(scene);
		window.show();
		
	}
	
	public static void generateTable() { //still need class/database to retrieve information from
		//Title column
        titleColumn = new TableColumn<>("Title");
        titleColumn.setMinWidth(100);
        //titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        //User Name column
        usernameColumn = new TableColumn<>("User Name");
        usernameColumn.setMinWidth(100);
        //usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        //Password column
        passwordColumn = new TableColumn<>("Password");
        passwordColumn.setMinWidth(100);
        //passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        
        //TableView
        myTable = new TableView<String>();
        //myTable.setItems();
        myTable.getColumns().addAll(titleColumn, usernameColumn, passwordColumn);
	}
	
	public static void generateTopMenu() {
		fileMenu = new Menu("File");
		editMenu = new Menu("Edit");
		viewMenu = new Menu("View");
		helpMenu = new Menu("Help");
		
		
		menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
		
		
	}

}
