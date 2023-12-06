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
import javafx.scene.layout.Background;
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
	
	private ComboBox<String> productComboBox;
	private TextField stockTextField;
	private TextField priceTextField;
	private Product.PRODUCT_INFO[] allProducts;
	private VBox productVBox;
	private Button logoutBtn;
	private ScrollPane sp;
	private Text errorText;
	
	private Seller seller;
	
	public Scene getScene() {
		Scene s = null;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("SellerMainUI.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("user_id", CurrentUser.getUserId())).first();
			
			if(doc == null) {
				return s;
			}
			
			this.seller = new Seller(
					doc.get("first_name").toString(), 
					doc.get("last_name").toString(), 
					CurrentUser.getUserId(), 
					doc.get("email_id").toString(), 
					null
					);
						
			Button addBtn = (Button) root.lookup("#addBtn");
			this.productComboBox = (ComboBox<String>) root.lookup("#productComboBox");
			this.stockTextField = (TextField) root.lookup("#stockTextField");
			this.priceTextField = (TextField) root.lookup("#priceTextField");
			this.logoutBtn = (Button) root.lookup("#logoutBtn");
			this.errorText = (Text) root.lookup("#errorText");

			
			VBox currentProductVBox = (VBox) root.lookup("#currentProductVBox");
			this.sp = new ScrollPane();
			currentProductVBox.getChildren().add(sp);
			this.productVBox = new VBox(25);
			this.sp.setContent(productVBox);
			this.sp.minWidthProperty().bind(this.productVBox.widthProperty().add(20));
			this.sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
			this.sp.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));
			this.sp.getStyleClass().add("scroll-pane");
			this.productVBox.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));
			
			
			this.sp.setVisible(false);
						
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
	
	private HBox getProduct(Product.PRODUCT_INFO productInfo, int price, double rating, int stock, int sold) {
		VBox productDataBox = new VBox(5);
		
		Image productImage = new Image(productInfo.getImagePath());
		ImageView productImageView = new ImageView();
		productImageView.setImage(productImage);
		productImageView.setFitHeight(100);
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
		productBox.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));
		
		return productBox;
	}
	
	private void renderProducts() {
		Document inventoryDoc = DBConnection.getCollection("Inventory").find(eq("sellerId", CurrentUser.getUserId())).first();
		if(inventoryDoc == null) {
			return;
		}
		this.productVBox.getChildren().clear();
		List<Document> inventoryList = inventoryDoc.getList("inventory", Document.class);
		
		if(inventoryList.size() > 0) {
			this.sp.setVisible(true);
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
			this.productVBox.getChildren().add(productBox);
		}
	}
	
	class AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			String productName = (String)productComboBox.getValue();
			String stockString = stockTextField.getText();
			String priceString = priceTextField.getText();
			
			if(productName == null) {
				errorText.setText("Please select a product");
				errorText.setFill(Color.RED);
				return;
			}
			
			if(!stockString.matches("\\d+")) {
				errorText.setText("Invalid stock amount");
				errorText.setFill(Color.RED);
				return;
			}
			
			int stock = Integer.parseInt(stockString);
			if(stock == 0) {
				errorText.setText("Stock cannot be 0");
				errorText.setFill(Color.RED);
				return;
			}
			
			if(!priceString.matches("\\d+")) {
				errorText.setText("Invalid price");
				errorText.setFill(Color.RED);
				return;
			}
			
			int price = Integer.parseInt(priceString);
			if(price == 0) {
				errorText.setText("Price cannot be 0$");
				errorText.setFill(Color.RED);
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
				errorText.setText("Selected product not found");
				errorText.setFill(Color.RED);
				return;
			}
			
			seller.addProduct(product);
			
			errorText.setText("Product is created!");
			errorText.setFill(Color.GREEN);
			
			renderProducts();
		}
	}
}
