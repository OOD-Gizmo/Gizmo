package application.Admin;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.mongodb.client.result.DeleteResult;

import application.DBConnection;
import application.Main;
import application.DTO.User.USER_TYPE;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

public class AdminMainUI {
	
	Button logoutBtn;
	TextField customerIdTextField;
	TextField sellerIdTextField;
	Button removeCustomerBtn;
	Button removeSellerBtn;
	
	public Scene getScene() {
        Parent root;
        Scene s = null;
		try {
			root = FXMLLoader.load(getClass().getResource("AdminMainUI.fxml"));
			
			logoutBtn = (Button) root.lookup("#logoutBtn");
			customerIdTextField = (TextField) root.lookup("#customerIdTextField");
			sellerIdTextField = (TextField) root.lookup("#sellerIdTextField");
			removeCustomerBtn = (Button) root.lookup("#removeCustomerBtn");
			removeSellerBtn = (Button) root.lookup("#removeSellerBtn");
			
			logoutBtn.setOnAction(e -> {
				Main.logout();
			});
			
			removeCustomerBtn.setOnAction(e -> {
				removeCustomer(customerIdTextField.getText());
			});
			
			removeSellerBtn.setOnAction(e -> {
				removeSeller(sellerIdTextField.getText());
			});
			
			
			
			s = new Scene(root, 1280, 720);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return s;
      
    }
	
	void removeCustomer(String id) {
		ObjectId userObjectId = DBConnection.getCollection("Users").find(eq("user_id", id)).first().getObjectId("_id");

		if(userObjectId == null) {
			return;
		}
		
		DeleteResult result = DBConnection.getCollection("Users").deleteOne(and(eq("_id", userObjectId), eq("type", USER_TYPE.BUYER.getTypeInt())));
		if(result.getDeletedCount() > 0) {
			result = DBConnection.getCollection("Purchases").deleteOne(eq("customerId", userObjectId));
		}
	}
	
	void removeSeller(String id) {
		ObjectId userObjectId = DBConnection.getCollection("Users").find(eq("user_id", id)).first().getObjectId("_id");

		if(userObjectId == null) {
			return;
		}
		
		DeleteResult result = DBConnection.getCollection("Users").deleteOne(and(eq("_id", userObjectId), eq("type", USER_TYPE.SELLER.getTypeInt())));
		if(result.getDeletedCount() > 0) {
			result = DBConnection.getCollection("Inventory").deleteOne(eq("sellerId", userObjectId));
		}
	}
	

}
