/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passguardui;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author yusuf
 */
public class PassGuardMainController implements Initializable {

    private final static ObservableList<AccountInfo> allAccounts = FXCollections.observableArrayList();
    
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
            allAccounts.add(new AccountInfo(account, "**********", "**********", "**********"));
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
            window.setTitle("Edit Account Information");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setScene(scene);
            window.show();
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
    
}
