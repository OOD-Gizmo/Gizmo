package application.Admin;

import java.io.IOException;

import org.bson.Document;

import application.DBConnection;
import application.Main;
import application.DTO.Admin;
import application.DTO.CurrentUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static com.mongodb.client.model.Filters.eq;

public class AdminMainUI {
	
	private Button logoutBtn;
	private TextField customerIdTextField;
	private TextField sellerIdTextField;
	private Button removeCustomerBtn;
	private Button removeSellerBtn;
	
	private Admin admin;
	
	public Scene getScene() {
        Parent root;
        Scene s = null;
		try {
			root = FXMLLoader.load(getClass().getResource("AdminMainUI.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("user_id", CurrentUser.getUserId())).first();

			if(doc == null) {
				return s;
			}
			
			this.admin = new Admin(
					doc.get("first_name").toString(), 
					doc.get("last_name").toString(), 
					CurrentUser.getUserId(), 
					doc.get("email_id").toString(), 
					null
			);
			
			logoutBtn = (Button) root.lookup("#logoutBtn");
			customerIdTextField = (TextField) root.lookup("#customerIdTextField");
			sellerIdTextField = (TextField) root.lookup("#sellerIdTextField");
			removeCustomerBtn = (Button) root.lookup("#removeCustomerBtn");
			removeSellerBtn = (Button) root.lookup("#removeSellerBtn");
			
			logoutBtn.setOnAction(e -> {
				Main.logout();
			});
			
			removeCustomerBtn.setOnAction(e -> {
				this.admin.removeCustomer(customerIdTextField.getText());
			});
			
			removeSellerBtn.setOnAction(e -> {
				this.admin.removeSeller(sellerIdTextField.getText());
			});
			
			
			
			s = new Scene(root, 1280, 720);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return s;
    }
}
