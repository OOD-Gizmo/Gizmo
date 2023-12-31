package application.Customer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import application.DBConnection;
import application.Main;
import application.DTO.Buyer;
import application.DTO.CurrentProduct;
import application.DTO.CurrentUser;
import application.DTO.Product;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CustomerProductUI {
	
	public enum RedirectionFrom {
		SEARCHPAGE,
		MAINPAGE
	}
	
	private Button backBtn;
	private Text productNameText;
	private ImageView productImageView;
	private Text specificationText;
	private Text priceText;
	private Text ratingText;
	private Text leftText;
	private Text sellerNameText;
	private Button purchaseBtn;
	private VBox rateVBox;
	private Text statusText;
	private Text rateStatusText;
	private Button rateBtn1;
	private Button rateBtn2;
	private Button rateBtn3;
	private Button rateBtn4;
	private Button rateBtn5;
	 
	private String sellerId;
	private Product product;
	private Product.PRODUCT_INFO[] allProducts = Product.PRODUCT_INFO.values();
	private String sellerName;
	private static RedirectionFrom redirectionFrom = RedirectionFrom.MAINPAGE;
	private boolean triedToPurchase = false;
	private boolean isPurchaseSuccessful = true;
	private boolean isPurchasedBefore = false;
	private boolean isRatedBefore = false;
	private boolean isRateSuccessful = true;
	
	Buyer buyer;
	
	public Scene getScene() {
		Scene s = null;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("CustomerProductUI.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("user_id", CurrentUser.getUserId())).first();
			
			if(doc == null) {
				return s;
			}
			
			this.buyer = new Buyer(
					doc.get("first_name").toString(), 
					doc.get("last_name").toString(), 
					CurrentUser.getUserId(), 
					doc.get("email_id").toString(), 
					null
					);
			
			this.backBtn = (Button) root.lookup("#backBtn");
			this.productNameText = (Text) root.lookup("#productNameText");
			this.productImageView = (ImageView) root.lookup("#productImageView");
			this.specificationText = (Text) root.lookup("#specificationText");
			this.priceText = (Text) root.lookup("#priceText");
			this.ratingText = (Text) root.lookup("#ratingText");
			this.leftText = (Text) root.lookup("#leftText");
			this.sellerNameText = (Text) root.lookup("#sellerNameText");
			this.purchaseBtn = (Button) root.lookup("#purchaseBtn");
			this.rateVBox = (VBox) root.lookup("#rateVBox");
			this.statusText = (Text) root.lookup("#statusText");
			this.rateStatusText = (Text) root.lookup("#rateStatusText");
			this.rateBtn1 = (Button) root.lookup("#rateBtn1");
			this.rateBtn2 = (Button) root.lookup("#rateBtn2");
			this.rateBtn3 = (Button) root.lookup("#rateBtn3");
			this.rateBtn4 = (Button) root.lookup("#rateBtn4");
			this.rateBtn5 = (Button) root.lookup("#rateBtn5");
						
			this.isPurchasedBefore = isPurchasedBeforeQuery();
			this.isRatedBefore = isPurchasedBefore && isRatedBeforeQuery();
			
			renderProduct();
			
			this.backBtn.setOnAction(e -> {
				back();
			});
			
			this.purchaseBtn.setOnAction(e -> {
				purchase();
			});
			
			Button[] rateButtonArray = { rateBtn1, rateBtn2, rateBtn3, rateBtn4, rateBtn5 };
			for(Button btn : rateButtonArray) {
				btn.setOnAction(e -> {
					Button targetBtn = (Button)e.getTarget();
					rate(targetBtn.getId());
				});
			}
			
			s = new Scene(root,1280,720);
			s.getStylesheets().add(getClass().getResource("CustomerMainUI.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return s;
	}
	
	private void renderProduct() {
		product = this.buyer.getProductById(CurrentProduct.getProductId(), allProducts);
		sellerId = product.getSellerId();
		
		Document sellerDoc = DBConnection.getCollection("Users").find(eq("user_id", sellerId)).first();
		sellerName = sellerDoc.getString("first_name") + " " + sellerDoc.getString("last_name");
		
		productNameText.setText(product.getProductInfo().getName());
		productImageView.setImage(new Image(product.getProductInfo().getImagePath()));
		specificationText.setText(product.getProductInfo().getSpecifications());
		priceText.setText("Price : $" + product.getPrice());
		ratingText.setText("Rating : " + String.format("%.1f", product.getRating()) + "/5");
		leftText.setText("Stock Left : " + product.getStock());
		sellerNameText.setText("Sold By : " + sellerName);
		
		if(!isPurchasedBefore) {
			rateVBox.setVisible(false);
		} else {
			if(!isRateSuccessful) {
				rateVBox.setVisible(true);
				rateVBox.setDisable(true);
			}
			else if(isRatedBefore) {
				rateVBox.setVisible(true);
				rateVBox.setDisable(true);
				rateStatusText.setVisible(true);
			} else {
				rateVBox.setVisible(true);
			}
		}
		
		if(triedToPurchase) {
			statusText.setVisible(true);
			
			if(isPurchaseSuccessful) {
				statusText.setText("Purchased Successfully!");
				statusText.setFill(Color.GREEN);
			} else {
				statusText.setText("Product was already out of stock");
				statusText.setFill(Color.RED);
			}
		}
		
		if(product.getStock() == 0) {
			purchaseBtn.setText("SOLD OUT");
			purchaseBtn.setStyle("-fx-background-color: grey;");
			purchaseBtn.setDisable(true);
			
			Bson filter = and(eq("sellerId", sellerId), eq("inventory._id", CurrentProduct.getProductId()));
			Bson update = Updates.pull("inventory", new Document("_id", CurrentProduct.getProductId()));
			UpdateResult result = DBConnection.getCollection("Inventory").updateOne(filter, update);
		}
	}
	
	private void purchase() {
		System.out.print("clicked!");
		triedToPurchase = true;
		Bson filter = and(eq("sellerId", sellerId), eq("inventory._id", CurrentProduct.getProductId()), gt("inventory.stock", 0));
		Bson update = Updates.inc("inventory.$.stock", -1);
		
		UpdateResult result = DBConnection.getCollection("Inventory").updateOne(filter, update);
				
		if(result.getMatchedCount() == 0) { // Either product does not exist or stock is 0, in both cases we try to remove the product from inventory
			product.setStock(0);
			isPurchaseSuccessful = false;
		} else {
			// purchase successful
			
			if(!isPurchasedBefore) {
				JSONObject purchaseJson = new JSONObject(); 
				purchaseJson.put("id", CurrentProduct.getProductId());
				purchaseJson.put("hasRated", false);
								
				Bson purchaseFilter = and(eq("customerId", CurrentUser.getUserId()));
				Bson purchaseUpdate = Updates.push("purchases", purchaseJson);
				UpdateOptions options = new UpdateOptions().upsert(true);
				
				UpdateResult purchaseResult = DBConnection.getCollection("Purchases").updateOne(purchaseFilter, purchaseUpdate, options);
				System.out.println(purchaseResult);
				if(purchaseResult.getModifiedCount() > 0 || purchaseResult.getUpsertedId() != null) {
					isPurchasedBefore = true;
				}
			}
		}
		
		renderProduct();
	}
		
	private boolean isPurchasedBeforeQuery() {
		Document purchaseDoc = DBConnection.getCollection("Purchases").find(and(eq("customerId", CurrentUser.getUserId()), eq("purchases.id", CurrentProduct.getProductId()))).first();
		return purchaseDoc != null;
	}
	
	private boolean isRatedBeforeQuery() {
		AggregateIterable<Document> purchaseDocs = DBConnection.getCollection("Purchases")
				.aggregate(Arrays.asList(
						Aggregates.match(eq("customerId", CurrentUser.getUserId())),
						Aggregates.unwind("$purchases"), 
						Aggregates.match(eq("purchases.id", CurrentProduct.getProductId())), 
						Aggregates.limit(1)));
		for(Document purchaseDoc : purchaseDocs) {
			if(purchaseDoc != null) {
				Document purchaseItem = (Document) purchaseDoc.get("purchases");
				return (boolean) purchaseItem.get("hasRated");
			}
		}
		
		return false;
	}
	
	private void back() {
		if(redirectionFrom == RedirectionFrom.MAINPAGE) {
			Main.setCustomerMainScene();
		} else {
			Main.setCustomerSearchScene();
		}
	}
	
	private void rate(String id) {
		int rating = 0;
		
		switch(id) {
			case("rateBtn1"):
				rating = 1;
				break;
				
			case("rateBtn2"):
				rating = 2;
				break;
				
			case("rateBtn3"):
				rating = 3;
				break;
				
			case("rateBtn4"):
				rating = 4;
				break;
				
			case("rateBtn5"):
				rating = 5;
				break;
		}
		
		Bson filterInventory = and(eq("sellerId", sellerId), eq("inventory._id", CurrentProduct.getProductId()));
		Bson updateRatingList = Updates.push("inventory.$.ratingList", rating);
		UpdateResult addRatingResult = DBConnection.getCollection("Inventory").updateOne(filterInventory, updateRatingList);
				
		if(addRatingResult.getModifiedCount() == 0) {
			isRateSuccessful = false;
			return;
		}
		
		AggregateIterable<Document> inventoryDocs = DBConnection.getCollection("Inventory")
				.aggregate(Arrays.asList(
						Aggregates.match(eq("sellerId", sellerId)),
						Aggregates.unwind("$inventory"), 
						Aggregates.match(eq("inventory._id", CurrentProduct.getProductId())), 
						Aggregates.limit(1)));
		
		List<Integer> ratingList = null;
		for(Document inventory : inventoryDocs) {
			if(inventory == null) {
				isRateSuccessful = false;
				return;
			}
			
			Document product = (Document) inventory.get("inventory");
			ratingList = product.getList("ratingList", Integer.class);
		}
		
		int sum = 0;
		for(int rate : ratingList) {
			sum += rate;
		}
		
		double updatedRating = ((double)sum / (double)(ratingList.size() == 0 ? 1 : ratingList.size())); //avoiding 0/0 division
		updatedRating = Math.floor(updatedRating * 10) / 10;
		
		Bson updateProductRating = Updates.set("inventory.$.rating", updatedRating);
		UpdateResult setNewRatingResult = DBConnection.getCollection("Inventory").updateOne(filterInventory, updateProductRating);
				
		if(setNewRatingResult.getMatchedCount() == 0) {
			isRateSuccessful = false;
			return;
		}
		
		Bson filterPurchases = and(eq("customerId", CurrentUser.getUserId()), eq("purchases.id", CurrentProduct.getProductId()));
		Bson updateRatingState = Updates.set("purchases.$.hasRated", true);
		UpdateResult setRatingStateResult = DBConnection.getCollection("Purchases").updateOne(filterPurchases, updateRatingState);
		
		if(setRatingStateResult.getModifiedCount() == 0) {
			isRateSuccessful = false;
			return;
		}
		
		isRatedBefore = true;
		renderProduct();
	}
	
	public static void setRedirectFrom(RedirectionFrom redirectFrom) {
		redirectionFrom = redirectFrom;
	}
}
