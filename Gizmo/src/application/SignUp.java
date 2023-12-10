package application;

import static com.mongodb.client.model.Filters.eq;

import java.util.Arrays;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoWriteException;
import com.mongodb.client.result.InsertOneResult;

import application.DTO.Admin;
import application.DTO.AuthHandler;
import application.DTO.Seller;
import application.DTO.Buyer;
import application.DTO.CurrentUser;
import application.DTO.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class SignUp {
	ToggleButton backToSignInButton;
	ToggleButton signupBtn;
	TextField firstNameText;
	TextField lastNameText;
	TextField emailText;
	TextField userText;
	TextField passwordText;
	TextField confirmPasswordText;
	Text errorText;	
	ComboBox<String> userType;
	
	@SuppressWarnings("unchecked")
	public Scene getScene() {
		Scene scene = null;		
		try {
			Parent rootParent = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
		
			this.backToSignInButton = (ToggleButton) rootParent.lookup("#backToSignInButton");
			this.signupBtn = (ToggleButton) rootParent.lookup("#signupBtn");

			this.firstNameText = (TextField) rootParent.lookup("#firstNameText");
			this.lastNameText = (TextField) rootParent.lookup("#lastNameText");
			this.emailText = (TextField) rootParent.lookup("#emailText");
			this.userText = (TextField) rootParent.lookup("#userText");
			this.emailText = (TextField) rootParent.lookup("#emailText");
			this.passwordText = (PasswordField) rootParent.lookup("#passwordText");
			this.confirmPasswordText = (PasswordField) rootParent.lookup("#confirmPasswordText");
			this.errorText = (Text) rootParent.lookup("#errorText");
			this.userType = (ComboBox<String>) rootParent.lookup("#typeComboBox");
			
			
			this.signupBtn.setOnAction(new SignUpHandler());
			this.backToSignInButton.setOnAction(new BackToHomeBtnHAndler());
			this.userType.getItems().addAll(
					"Buyer",
					"Seller"
					);
			
			scene = new Scene(rootParent, 1280, 720);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scene;
	}
	
	class BackToHomeBtnHAndler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			Main.setLoginScene();
		} 
		
	}
	
	class SignUpHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			// empty fields check
			if(firstNameText.getText().toString().equals("") || lastNameText.getText().toString().equals("") || 
					emailText.getText().toString().equals("") || passwordText.getText().toString().equals("") || confirmPasswordText.getText().toString().equals("")) {
			    errorText.setStyle("-fx-text-fill: red;");
				errorText.setText("All fields must have some value");
				return;
			}
			
			// user type selection check
			if(userType.getValue() == null || userType.getValue().equals("") || userType.getValue().isEmpty()) {
			    errorText.setStyle("-fx-text-fill: red;");
				errorText.setText("Please select a user type");
				return;
			}
			
			// password check
			if(!passwordText.getText().toString().equals(confirmPasswordText.getText().toString())) {
			    errorText.setStyle("-fx-text-fill: red;");
				errorText.setText("Passwords do not match");
				return;
			}
			
			// depending upon user Create the user type variable
			User newUser = null;
			if(userType.getValue() == "Admin") {
				newUser = (Admin) new Admin(firstNameText.getText().toString(), lastNameText.getText().toString(), userText.getText(), emailText.getText().toString(), null);
			}else if(userType.getValue() == "Seller") {
				newUser = (Seller) new Seller(firstNameText.getText().toString(), lastNameText.getText().toString(), userText.getText(), emailText.getText().toString(), null);
			}else {
				newUser = (Buyer) new Buyer(firstNameText.getText().toString(), lastNameText.getText().toString(), userText.getText(), emailText.getText().toString(), null);
			}
				
			try {
				DBConnection.getCollection("Users").insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("user_id", newUser.getUserID())
                        .append("password", AuthHandler.getPasswordHash(passwordText.getText()))
                        .append("first_name", newUser.getFirstName())
                        .append("last_name", newUser.getLastName())
                        .append("email_id", newUser.getEmailID())
                        .append("type", newUser.getUser_TYPE().getTypeInt())
						);
			    errorText.setFill(Paint.valueOf("green"));
				errorText.setText("User Account created Successfully");
			} catch (MongoWriteException e) {
				errorText.setText("Username already exists. Please choose another one.");
			}				
		}
		
	}

}
