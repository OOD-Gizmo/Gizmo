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
	
	static TextField useridText;
	static PasswordField passwordText;
	Text errorText;
	ToggleButton loginBtn;
	ToggleButton signUpButton;
	
	static SellerMainUI sellerMainUI;
	static CustomerMainUI customerMainUI;
	static CustomerSearchResultUI customerSearchResultUI;
	static CustomerProductUI customerProductUI;
	static AdminMainUI adminMainUI;
	
	static Scene mainScene;
	static Stage mainstage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			mainstage = primaryStage;
			
			sellerMainUI = new SellerMainUI();
			customerMainUI = new CustomerMainUI();
			customerSearchResultUI = new CustomerSearchResultUI();
			customerProductUI = new CustomerProductUI();
			
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
//			CurrentProduct.setProduct(new ObjectId("6565330bd48aba50fa520eb7"));
//			setScene(customerMainUI.getScene());
			
			
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
			mainstage.setScene(scene);
		}
	}
	
	public static void setScene(Scene scene) {
		mainstage.setScene(scene);
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
