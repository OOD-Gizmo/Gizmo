package application;

import org.bson.Document;
import org.bson.types.ObjectId;

import application.Customer.CustomerMainUI;
import application.Customer.CustomerSearchResultUI;
import application.DTO.AuthHandler;
import application.DTO.CurrentUser;
import application.DTO.Product;
import application.Seller.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;

public class Main extends Application {
	
	TextField useridText;
	PasswordField passwordText;
	Text errorText;
	ToggleButton loginBtn;
	ToggleButton signUpButton;
	
	static SellerMainUI sellerMainUI;
	static CustomerMainUI customerMainUI;
	static CustomerSearchResultUI customerSearchResultUI;
	
	
	static Scene mainScene;
	static Stage mainstage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			mainstage = primaryStage;
			
			sellerMainUI = new SellerMainUI();
			customerMainUI = new CustomerMainUI();
			customerSearchResultUI = new CustomerSearchResultUI();
			
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			
			DBConnection.initClient();
			
			useridText = (TextField) root.lookup("#userText");
			passwordText = (PasswordField) root.lookup("#passwordText");
			errorText = (Text) root.lookup("#errorText");
			loginBtn = (ToggleButton) root.lookup("#loginBtn");
			signUpButton = (ToggleButton) root.lookup("#signUpButton");
			
			loginBtn.setOnAction(new LoginHandler());
			signUpButton.setOnAction(new RedirectToSignup());
			
			mainScene = new Scene(root,1280,720);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(mainScene);
//			CurrentUser.setUserId(new ObjectId("65537af362d1923857f60468")); //for testing a scene directly
//			CustomerSearchResultUI.setSearchedProduct(Product.PRODUCT_INFO.IPHONE12);
//			setScene(customerSearchResultUI.getScene());
			
			
			primaryStage.show();		
			primaryStage.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		DBConnection.closeConnection();
		System.out.println("Connection Closed");
	}
	
	class LoginHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			String userId = useridText.getText();
			String password = passwordText.getText();
			
			if(userId.isEmpty() || password.isEmpty()) {
				errorText.setText("User Id and Password cannot be empty");
				return;
			}
			
			String hashedPassword = AuthHandler.getPasswordHash(password);
						
			Document doc = DBConnection.getCollection("Users").find(eq("user_id", userId)).first();
			if(doc != null) {
				if(doc.get("password").equals(hashedPassword)) {
					CurrentUser.setUserId(doc.getObjectId("_id"));
					System.out.println(doc);
					errorText.setText("");
					
//					stage.setScene(sellerMainUI.getScene()); TODO: Add user type check					
					
				} else {
					System.out.println("Incorrect Password");
					errorText.setText("The password is incorrect");
				}
			} else {
				System.out.println("No doc found for user: " + userId + " and pass: " + password);
				errorText.setText("This user id does not exist");
			}
		}
	}
	
	class RedirectToSignup implements EventHandler<ActionEvent>{
		public void handle(ActionEvent evento) {
			Scene scene = (new SignUp()).getScene();
			stage.setScene(scene);
		}
	}
	
	public void setSceneForTesting(Scene scene) {
		stage.setScene(scene);
	public static void setScene(Scene scene) {
		mainstage.setScene(scene);
	}
	
	public static void setCustomerMainScene() {
		customerMainUI = new CustomerMainUI();
		mainstage.setScene(customerMainUI.getScene());
	}
	
	public static void setCustomerSearchScene() {
		customerSearchResultUI = new CustomerSearchResultUI();
		mainstage.setScene(customerSearchResultUI.getScene());
	}
	
	public static void logout() {
		try {
			Parent root = FXMLLoader.load(Main.class.getResource("Login.fxml"));
			CurrentUser.setUserId(null);
			mainstage.setScene(mainScene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
