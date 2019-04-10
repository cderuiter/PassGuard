/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sqlite.SQLiteHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import static passguardui.PassGuardLogin.iconPath;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardMainController implements Initializable {

    private static ObservableList<AccountInfo> allAccounts = FXCollections.observableArrayList();
    
    @FXML
    private TextField searchAccountTextField;
    @FXML
    private Button searchAccountButton;
    @FXML
    private Button addAccountButton;
    @FXML
    private Button editAccountButton;
    @FXML
    private Button retrieveAccountButton;
    @FXML
    private Button removeAccountButton;
    
    @FXML
    private Menu editMenuButton;
    @FXML
    private MenuItem addAccount;
    @FXML
    private MenuItem editAccount;
    @FXML
    private MenuItem retrieveAccount;
    @FXML
    private MenuItem removeAccount;
    @FXML
    private MenuItem close;
    @FXML
    private MenuItem logOut;
    @FXML
    private MenuItem about;
    
    @FXML
    private ContextMenu tableRightClickMenu; //right click on table
    @FXML
    private MenuItem addEntryRC;
    @FXML
    private MenuItem editEntryRC; //right click on table
    @FXML
    private MenuItem retrieveEntryRC; //right click on table
    @FXML
    private MenuItem removeEntryRC; //right click on table
   
    @FXML
    private TableView<AccountInfo> accountTable; 
    @FXML
    private TableColumn<AccountInfo, String> accountColumn; 
    @FXML
    private TableColumn<AccountInfo, String> usernameColumn; 
    @FXML
    private TableColumn<AccountInfo, String> passwordColumn; 
    @FXML
    private TableColumn<AccountInfo, String> notesColumn; 
    
    
    public void start() throws Exception{
        Stage window = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("PassGuardMainFXML.fxml"));
        Scene scene = new Scene(root);
        
        window.resizableProperty().setValue(false); //makes it so you can not maximize
        
        window.setOnCloseRequest((event) -> {
            closeMenuItem(); 
        });
        
        Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
        window.getIcons().add(icon);
        
        window.setTitle("PassGuard");
        window.setScene(scene);
        window.show();
        
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //this function runs when the window is launched, puts all accounts from the database into the table
        SQLiteHelper.SQLLiteDatabaseConnection();
        allAccounts = FXCollections.observableArrayList();
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        accountTable.setItems(getAccountsFromDB());
        
    }
    
    public static ObservableList<AccountInfo> getAllAccounts(){
        return allAccounts; 
    }
    
    private static ObservableList<AccountInfo> getAccountsFromDB(){
        
        ArrayList<String> myAccounts = SQLiteHelper.getAllAccounts();
        
        for(String account : myAccounts){
            String username = SQLiteHelper.getUsername(account);
            String notes = SQLiteHelper.getNotes(account);
            allAccounts.add(new AccountInfo(account, username, "**********", notes));
        }
        return allAccounts;
    }

    @FXML
    private void handleAddAccountButton(){
        PassGuardAddAccountController addAccountWindow = new PassGuardAddAccountController();
        try{
            addAccountWindow.start();
        }
        catch(Exception e){
            Platform.exit();
        }
    }
    
    @FXML
    private void handleEditAccountButton(){
        try{
            String accountName = accountTable.getSelectionModel().getSelectedItem().getAccount();
            SQLiteHelper.SQLLiteDatabaseConnection();
            String username = SQLiteHelper.getUsername(accountName);
            String password = SQLiteHelper.getPassword(accountName);
            String notes = SQLiteHelper.getNotes(accountName);
        
            
            Stage window = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PassGuardEditAccountInfoFXML.fxml").openStream());
            Scene scene = new Scene(root);
            PassGuardEditAccountInfoController editAccountInfoWindow = (PassGuardEditAccountInfoController) loader.getController();
            editAccountInfoWindow.setCurrentInfo(accountName, username, password, notes);
            Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
            window.resizableProperty().setValue(false); //makes it so you can not maximize
            window.getIcons().add(icon);
            window.setTitle("Edit Account Information");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            
            window.showAndWait();
            allAccounts = FXCollections.observableArrayList();
            accountTable.setItems(getAccountsFromDB());
        }
        catch(IOException | RuntimeException e){
            
        }
    }
    
    
    @FXML
    private void handleRemoveAccountButton(){
        
        SQLiteHelper.SQLLiteDatabaseConnection();
        ObservableList<AccountInfo> accountSelected, everyAccount;
        String accountToDelete;
        
        try{
           everyAccount = accountTable.getItems();
            accountSelected = accountTable.getSelectionModel().getSelectedItems();
            accountToDelete = accountTable.getSelectionModel().getSelectedItem().getAccount();
            SQLiteHelper.deleteAccount(accountToDelete);
            accountSelected.forEach(everyAccount::remove); 
        }
        catch(RuntimeException e){
            
        }
    }
    
    @FXML
    private void handleRetrieveAccountInfoButton(){
   
        try{
            String accountName = accountTable.getSelectionModel().getSelectedItem().getAccount();
            SQLiteHelper.SQLLiteDatabaseConnection();
            String username = SQLiteHelper.getUsername(accountName);
            String password = SQLiteHelper.getPassword(accountName);
            String notes = SQLiteHelper.getNotes(accountName);
        
            
            Stage window = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PassGuardRetrieveAccountInfoFXML.fxml").openStream());
            Scene scene = new Scene(root);
            PassGuardRetrieveAccountInfoController accountInfoWindow = (PassGuardRetrieveAccountInfoController) loader.getController();
            accountInfoWindow.setInfo(accountName, username, password, notes);
            
            Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
            window.getIcons().add(icon);
            window.resizableProperty().setValue(false); //makes it so you can not maximize
            window.setTitle("Account Information");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            window.show();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        catch(RuntimeException e){ //used to catch if user does not have anything selected when pressing the button
            //is there any need to do anything here, like give a pop up?
        }
        
    }
    
    @FXML
    private void handleSearchEnterPress(KeyEvent keyEvent){
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            handleSearchAccountButton();
        }
    }
    
    @FXML
    private void handleSearchAccountButton(){
        String searchedAccount = searchAccountTextField.getText().toLowerCase().trim();
        List<String> accountColumnData = new ArrayList<>();
        for(AccountInfo data : accountTable.getItems()){
            accountColumnData.add(accountColumn.getCellObservableValue(data).getValue().toLowerCase().trim());
        }
        int index = accountColumnData.indexOf(searchedAccount); //index = -1 if that string is not in the column
        accountTable.getSelectionModel().select(index, accountColumn);
        accountTable.scrollTo(index);
    }
    
    @FXML
    private void closeMenuItem(){
        if(ClipboardWaitThread.copyFlag){
            try{
                java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection emptySelection = new StringSelection("");
                clipboard.setContents(emptySelection, emptySelection);
            }
            catch(HeadlessException e){
                    
            }
        }
        Platform.exit();
    }
    
    @FXML
    private void handleEditMenuButton(){
        ObservableList<AccountInfo> accountSelected = accountTable.getSelectionModel().getSelectedItems();
        if(accountSelected.isEmpty()){
            editAccount.setDisable(true);
            removeAccount.setDisable(true);
            retrieveAccount.setDisable(true);
        }
        else{
            editAccount.setDisable(false);
            removeAccount.setDisable(false);
            retrieveAccount.setDisable(false);
        }
    }
    
    @FXML
    private void handleLogoutMenuItem(){
        
        if(ClipboardWaitThread.copyFlag){
            try{
                java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection emptySelection = new StringSelection("");
                clipboard.setContents(emptySelection, emptySelection);
            }
            catch(HeadlessException e){
                    
            }
        }
        
        Stage stage = (Stage) searchAccountButton.getScene().getWindow();
        stage.close();
        
        try{
            Stage loginWindow = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PassGuardLoginFXML.fxml").openStream());
            Scene scene = new Scene(root);
            Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
            loginWindow.getIcons().add(icon);
            loginWindow.resizableProperty().setValue(false); //makes it so you can not maximize
            loginWindow.setTitle("PassGuard Log-In");
            loginWindow.setScene(scene);
            loginWindow.show();
        }
        catch(IOException e){
            
        }
        
    }
    
    @FXML
    private void handleAboutMenuButton(){
        try{
            Stage loginWindow = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("PassGuardAboutFXML.fxml").openStream());
            Scene scene = new Scene(root);
            Image icon = new Image(this.getClass().getResourceAsStream(iconPath));
            loginWindow.resizableProperty().setValue(false); //makes it so you can not maximize
            loginWindow.getIcons().add(icon);
            loginWindow.setTitle("About PassGuard");
            loginWindow.setScene(scene);
            loginWindow.show();
        }
        catch(IOException e){
            
        }
    }
    
    @FXML
    private void handleTableRightClick(MouseEvent mouseClick){
        try{
            if(mouseClick.getButton() == MouseButton.SECONDARY){
                ObservableList<AccountInfo> accountSelected = accountTable.getSelectionModel().getSelectedItems();
                if(accountSelected.isEmpty()){
                    addEntryRC.setDisable(false);
                    editEntryRC.setDisable(true);
                    removeEntryRC.setDisable(true);
                    retrieveEntryRC.setDisable(true);
                }
                else{
                    addEntryRC.setDisable(false);
                    editEntryRC.setDisable(false);
                    removeEntryRC.setDisable(false);
                    retrieveEntryRC.setDisable(false);
                }
                tableRightClickMenu.show(tableRightClickMenu, mouseClick.getScreenX(), mouseClick.getScreenY());
            }
        }
        catch(Exception e){
                    
        }
    }
}
