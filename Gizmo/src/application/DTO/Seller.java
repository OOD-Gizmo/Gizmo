package application.DTO;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import application.DBConnection;

public class Seller extends User {
	public Seller(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.SELLER);
		
	}
	
	@SuppressWarnings("unchecked")
	public void addProduct(Product product) {
		Document inventoryDoc = DBConnection.getCollection("Inventory").find(eq("sellerId", this.userID)).first();
		if(inventoryDoc != null) {
			
			List<Document> inventoryList = inventoryDoc.getList("inventory", Document.class);
			boolean productFound = false;
			for(int i = 0; i < inventoryList.size(); i++) {
				if((int)inventoryList.get(i).get("id") == product.getProductInfo().getId() && (int)inventoryList.get(i).get("price") == product.getPrice()) {
					productFound = true;
					
					Bson filter = and(eq("sellerId", this.userID), and(eq("inventory.id", product.getProductInfo().getId()), eq("inventory.price", product.getPrice())));
					Bson update = Updates.inc("inventory.$.stock", product.getStock());
					
					UpdateResult result = DBConnection.getCollection("Inventory").updateOne(filter, update);
					System.out.print("Stock updated : " + result);
					
					break;
				}
			}
			
			if(!productFound) {
				
				JSONObject productJson = new JSONObject(); 
				productJson.put("_id", new ObjectId());
				productJson.put("id", product.getProductInfo().getId());
				productJson.put("stock", product.getStock());
				productJson.put("price", product.getPrice());
				productJson.put("sold", 0);
				productJson.put("rating", product.getRating());
				productJson.put("ratingList", Arrays.asList());
				
				Bson filter = and(eq("sellerId", this.userID));
				Bson update = Updates.push("inventory", productJson);
				UpdateResult result = DBConnection.getCollection("Inventory").updateOne(filter, update);
				System.out.print("Inventory updated : " + result);			
			}
			
		} else {
			JSONObject productJson = new JSONObject(); 
			productJson.put("_id", new ObjectId());
			productJson.put("id", product.getProductInfo().getId());
			productJson.put("stock", product.getStock());
			productJson.put("price", product.getPrice());
			productJson.put("sold", 0);
			productJson.put("rating", product.getRating());
			productJson.put("ratingList", Arrays.asList());
			
			InsertOneResult result = DBConnection.getCollection("Inventory").insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("sellerId", this.userID)
                    .append("inventory", Arrays.asList(productJson)));
			System.out.println("Success! Inserted document id: " + result.getInsertedId());
		}		
	}

}
