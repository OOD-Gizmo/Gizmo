package application.DTO;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import application.DBConnection;
import application.DTO.Product.PRODUCT_INFO;

public class Buyer extends User {
	public Buyer(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.BUYER);
	}
	
	public AggregateIterable<Document> getMainPageInventory() {
		AggregateIterable<Document> inventoryDocs = DBConnection.getCollection("Inventory").aggregate(Arrays.asList(Aggregates.match(gt("inventory.stock", 0)), Aggregates.sample(8)));
		return inventoryDocs;
	}
	
	public AggregateIterable<Document> getSearchedProduct(int id) {
		AggregateIterable<Document> productDocs = DBConnection.getCollection("Inventory")
				.aggregate(Arrays.asList(
						Aggregates.unwind("$inventory"), 
						Aggregates.match(eq("inventory.id", id)), 
						Aggregates.sort(Sorts.ascending("inventory.price")),
						Aggregates.limit(10),
						Aggregates.project(Projections.include("inventory"))));
		return productDocs;
	}
	
	public Product getProductById(ObjectId id, PRODUCT_INFO[] allProducts) {
		AggregateIterable<Document> productDocs = DBConnection.getCollection("Inventory")
				.aggregate(Arrays.asList(
						Aggregates.unwind("$inventory"), 
						Aggregates.match(eq("inventory._id", id)),
						Aggregates.limit(1)));
		
		Product product = null;
					
		for(Document prod : productDocs) {
			String sellerId = prod.get("sellerId").toString();
			Document inventory = (Document) prod.get("inventory");
			int productId = (int) inventory.get("id");
			int price = (int) inventory.get("price");
			int stock = (int) inventory.get("stock");
			double rating = (double) inventory.get("rating");
			
			Product.PRODUCT_INFO productInfo = null;
			
			for(Product.PRODUCT_INFO productInfoElem : allProducts) {
				if(productInfoElem.getId() == productId) {
					productInfo = productInfoElem;
					break;
				}
			}
			
			product = new Product(productInfo, price, stock, rating, sellerId);
		}
		
		return product;
		
	}
}
