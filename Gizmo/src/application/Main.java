package application;

import org.bson.Document;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

public class Main extends Application {
	
	TextField userid;
	TextField password;
	Button loginBtn;
		
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			
			DBConnection.initClient();
			
//			userid = (TextField) root.lookup("#userid");
//			password = (TextField) root.lookup("#password");
//			loginBtn = (Button) root.lookup("#login");
//			
//			loginBtn.setOnAction(new LoginHandler());
			
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	class LoginHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			String id = userid.getText();
			String pass = password.getText();
			
			Document doc = DBConnection.getCollection("Users").find(and(eq("user_id", id), eq("password", pass))).first();
			if(doc != null) {
				System.out.print("Doc found");
			} else {
				System.out.print("No doc found for user: " + id + " and pass: " + pass);
			}
		}
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
