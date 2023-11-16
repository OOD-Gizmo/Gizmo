package application;

import org.bson.Document;

import application.DTO.AuthHandler;

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
	
	TextField useridText;
	PasswordField passwordText;
	Text errorText;
	ToggleButton loginBtn;
		
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			
			DBConnection.initClient();
			
			useridText = (TextField) root.lookup("#userText");
			passwordText = (PasswordField) root.lookup("#passwordText");
			errorText = (Text) root.lookup("#errorText");
			loginBtn = (ToggleButton) root.lookup("#loginBtn");
			
			loginBtn.setOnAction(new LoginHandler());
			
			Scene scene = new Scene(root,1280,720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
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
					System.out.println("Login Successfull");
					errorText.setText("");
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
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
