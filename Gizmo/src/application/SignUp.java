package application;

import static com.mongodb.client.model.Filters.eq;

import java.awt.Button;

import org.bson.Document;

import application.DTO.CurrentUser;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
	
	public Scene getScene() {
		Scene scene = null;		
		try {
			Parent rootParent = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("_id", CurrentUser.getUserId())).first();
			if(doc == null)
				return scene; 
			this.backToSignInButton = (ToggleButton) rootParent.lookup("#backToSignInButton");
			this.signupBtn = (ToggleButton) rootParent.lookup("#signupBtn");

			this.firstNameText = (TextField) rootParent.lookup("#firstNameText");
			this.lastNameText = (TextField) rootParent.lookup("#firstNameText");
			this.emailText = (TextField) rootParent.lookup("#emailText");
			this.userText = (TextField) rootParent.lookup("#userText");
			this.emailText = (TextField) rootParent.lookup("#emailText");
			this.passwordText = (TextField) rootParent.lookup("passwordText");
			this.passwordText = (TextField) rootParent.lookup("confirmPasswordText");
			this.errorText = (Text) rootParent.lookup("#errorText");
			
			if(this.firstNameText.getText().toString() == "" || this.lastNameText.getText().toString() == "" || 
					this.emailText.getText().toString() == "" || this.passwordText.getText().toString() == "" || this.confirmPasswordText.getText().toString() == "" )
				this.errorText.setText("All fields must have some value"); 
			
				

			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return scene;
	}
	
	class BackToHomeBtnHAndler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
