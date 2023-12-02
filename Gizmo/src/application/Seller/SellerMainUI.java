package application.Seller;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
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
import application.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import application.DTO.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;


public class SellerMainUI {
	
	ComboBox<String> productComboBox;
	TextField stockTextField;
	TextField priceTextField;
	Product.PRODUCT_INFO[] allProducts;
	VBox productVBox;
	Button logoutBtn;
	ScrollPane sp;
	
	public Scene getScene() {
		Scene s = null;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("SellerMainUI.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("_id", CurrentUser.getUserId())).first();
			
			if(doc == null) {
				return s;
			}
						
			Button addBtn = (Button) root.lookup("#addBtn");
			productComboBox = (ComboBox<String>) root.lookup("#productComboBox");
			stockTextField = (TextField) root.lookup("#stockTextField");
			priceTextField = (TextField) root.lookup("#priceTextField");
			logoutBtn = (Button) root.lookup("#logoutBtn");

			
			VBox currentProductVBox = (VBox) root.lookup("#currentProductVBox");
			sp = new ScrollPane();
			currentProductVBox.getChildren().add(sp);
			productVBox = new VBox(25);
			sp.setContent(productVBox);
			sp.minWidthProperty().bind(productVBox.widthProperty().add(20));
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			
			
			sp.setVisible(false);
						
			allProducts = Product.PRODUCT_INFO.values();
			String[] allProductNames = new String[allProducts.length];
			
			for(int i = 0; i < allProducts.length; i++) {
				allProductNames[i] = allProducts[i].getName();				
			}
			
			productComboBox.getItems().addAll(allProductNames);
			
			logoutBtn.setOnAction(event -> {
				Main.logout();
			});
			
			addBtn.setOnAction(new AddHandler());
			renderProducts();
			
			s = new Scene(root, 1280,720);
			
			s.getStylesheets().add(getClass().getResource("SellerMainUI.css").toExternalForm());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return s;
	}
	
	public HBox getProduct(Product.PRODUCT_INFO productInfo, int price, double rating, int stock, int sold) {
		VBox productDataBox = new VBox(5);
		
		Image productImage = new Image(productInfo.getImagePath());
		ImageView productImageView = new ImageView();
		productImageView.setImage(productImage);
		productImageView.setPreserveRatio(true);
		productImageView.setFitWidth(100);
		

		Text nameText = new Text("NAME : " + productInfo.getName());
		nameText.setFont(Font.font("Avenir", 15));
		nameText.setFill(Paint.valueOf("#867d7d"));
		
		Text priceText = new Text("PRICE : $" + price);
		priceText.setFont(Font.font("Avenir", 15));
		priceText.setFill(Paint.valueOf("#867d7d"));

		Text ratingText = new Text("RATING : " + String.format("%.1f", rating));
		ratingText.setFont(Font.font("Avenir", 15));
		ratingText.setFill(Paint.valueOf("#867d7d"));

		Text stockText = new Text("STOCK LEFT : " + stock);
		stockText.setFont(Font.font("Avenir", 15));
		stockText.setFill(Paint.valueOf("#867d7d"));

		Text soldText = new Text("SOLD : " + sold);
		soldText.setFont(Font.font("Avenir", 15));
		soldText.setFill(Paint.valueOf("#867d7d"));

		
		productDataBox.getChildren().addAll(nameText, priceText, ratingText, stockText, soldText);
		
		HBox productBox = new HBox(45);
		
		productBox.getChildren().add(productImageView);
		productBox.getChildren().add(productDataBox);
		productBox.setId("productBox");
		
		return productBox;
	}
	
	public void renderProducts() {
		Document inventoryDoc = DBConnection.getCollection("Inventory").find(eq("sellerId", CurrentUser.getUserId())).first();
		if(inventoryDoc == null) {
			return;
		}
		productVBox.getChildren().clear();
		List<Document> inventoryList = inventoryDoc.getList("inventory", Document.class);
		
		if(inventoryList.size() > 0) {
			sp.setVisible(true);
		}
		
		for(int i = 0; i < inventoryList.size(); i++) {
			int productId = (int)inventoryList.get(i).get("id");
			Product.PRODUCT_INFO productInfo = null;
			
			for(int j = 0; j < allProducts.length; j++) {
				if(allProducts[j].getId() == productId) {
					productInfo = allProducts[j];
					break;
				}
			}
			
			if(productInfo == null) {
				continue;
			}
			
			int price = (int)inventoryList.get(i).get("price");
			double rating = (double)inventoryList.get(i).get("rating");
			int stock = (int)inventoryList.get(i).get("stock");
			int sold = (int)inventoryList.get(i).get("sold");
			
			HBox productBox = getProduct(productInfo, price, rating, stock, sold);
			productVBox.getChildren().add(productBox);
		}
	}
	
	class AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			String productName = (String)productComboBox.getValue();
			String stockString = stockTextField.getText();
			String priceString = priceTextField.getText();
			
			if(productName == null) {
				// error
				System.out.print("A product needs to be selected");
				return;
			}
			
			if(!stockString.matches("\\d+")) {
				// error
				System.out.print("Enter a valid number for stock");
				return;
			}
			
			int stock = Integer.parseInt(stockString);
			if(stock == 0) {
				// error
				System.out.print("Stock quantity cannot be 0");
				return;
			}
			
			if(!priceString.matches("\\d+")) {
				// error
				System.out.print("Enter a valid number for price");
				return;
			}
			
			int price = Integer.parseInt(priceString);
			if(price == 0) {
				// error
				System.out.print("price cannot be 0$");
				return;
			}
			
			Product product = null;
			for(int i = 0; i < allProducts.length; i++) {
				if(allProducts[i].getName().equals(productName)) {
					product = new Product(allProducts[i], price, stock);
					break;
				}
			}
			
			
			if(product == null) {
				// error - should not have happened
				System.out.print("product not found");
				return;
			}
			
			
			Document inventoryDoc = DBConnection.getCollection("Inventory").find(eq("sellerId", CurrentUser.getUserId())).first();
			if(inventoryDoc != null) {
				
				List<Document> inventoryList = inventoryDoc.getList("inventory", Document.class);
				boolean productFound = false;
				for(int i = 0; i < inventoryList.size(); i++) {
					if((int)inventoryList.get(i).get("id") == product.getProductInfo().getId() && (int)inventoryList.get(i).get("price") == product.getPrice()) {
						productFound = true;
						
						Bson filter = and(eq("sellerId", CurrentUser.getUserId()), and(eq("inventory.id", product.getProductInfo().getId()), eq("inventory.price", product.getPrice())));
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
					
					Bson filter = and(eq("sellerId", CurrentUser.getUserId()));
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
                        .append("sellerId", CurrentUser.getUserId())
                        .append("inventory", Arrays.asList(productJson)));
				System.out.println("Success! Inserted document id: " + result.getInsertedId());
			}		
			
			renderProducts();
		}
	}
}
