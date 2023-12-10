package application.DTO;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.result.DeleteResult;

import application.DBConnection;

public class Admin extends User{
	
	public Admin(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.ADMIN);
	
	}
	
	public void removeCustomer(String id) {
		String userId = (String)DBConnection.getCollection("Users").find(eq("user_id", id)).first().get("user_id");

		if(userId == null) {
			return;
		}
		
		DeleteResult result = DBConnection.getCollection("Users").deleteOne(and(eq("user_id", userId), eq("type", USER_TYPE.BUYER.getTypeInt())));
		if(result.getDeletedCount() > 0) {
			result = DBConnection.getCollection("Purchases").deleteOne(eq("customerId", userId));
		}
	}
	
	public void removeSeller(String id) {
		String userId = (String) DBConnection.getCollection("Users").find(eq("user_id", id)).first().get("user_id");

		if(userId == null) {
			return;
		}
		
		DeleteResult result = DBConnection.getCollection("Users").deleteOne(and(eq("user_id", userId), eq("type", USER_TYPE.SELLER.getTypeInt())));
		if(result.getDeletedCount() > 0) {
			result = DBConnection.getCollection("Inventory").deleteOne(eq("sellerId", userId));
		}
	}
}
