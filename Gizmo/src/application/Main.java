package application;

import org.bson.Document;

import application.Admin.AdminMainUI;
import application.Customer.CustomerMainUI;
import application.Customer.CustomerProductUI;
import application.Customer.CustomerSearchResultUI;
import application.DTO.AuthHandler;
import application.DTO.CurrentUser;
import application.DTO.User.USER_TYPE;
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


public class Main extends Application {
	
	private static TextField useridText;
	private static PasswordField passwordText;
	private Text errorText;
	private ToggleButton loginBtn;
	private ToggleButton signUpButton;
	
	private static SellerMainUI sellerMainUI;
	private static CustomerMainUI customerMainUI;
	private static CustomerSearchResultUI customerSearchResultUI;
	private static CustomerProductUI customerProductUI;
	private static AdminMainUI adminMainUI;
	
	private static Scene mainScene;
	private static Stage mainstage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			mainstage = primaryStage;
			
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
					CurrentUser.setUserId(doc.get("user_id").toString());
					System.out.println(doc);
					errorText.setText("");
					
					USER_TYPE userType = USER_TYPE.getEnumFromTypeInt((int)doc.get("type"));
					switch(userType) {
						case ADMIN:
							setAdminMainScene();
							break;
						
						case SELLER:
							setSellerMainScene();
							break;
						
						case BUYER:
							setCustomerMainScene();
							break;
						
						default:
							break;
					}					
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
		public void handle(ActionEvent event) {
			Scene scene = (new SignUp()).getScene();
			mainstage.setScene(scene);
		}
	}
	
	public static void setAdminMainScene() {
		adminMainUI = new AdminMainUI();
		mainstage.setScene(adminMainUI.getScene());
	}
	
	public static void setSellerMainScene() {
		sellerMainUI = new SellerMainUI();
		mainstage.setScene(sellerMainUI.getScene());
	}

	public static void setCustomerMainScene() {
		customerMainUI = new CustomerMainUI();
		mainstage.setScene(customerMainUI.getScene());
	}
	
	public static void setCustomerSearchScene() {
		customerSearchResultUI = new CustomerSearchResultUI();
		mainstage.setScene(customerSearchResultUI.getScene());
	}
	
	public static void setCustomerProductScene() {
		customerProductUI = new CustomerProductUI();
		mainstage.setScene(customerProductUI.getScene());
	}
	
	public static void setLoginScene() {
		mainstage.setScene(mainScene);
	}
	
	public static void logout() {
		CurrentUser.setUserId(null);
		setLoginScene();
		useridText.setText("");
		passwordText.setText("");
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
