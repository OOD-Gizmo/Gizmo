package application.Customer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import application.DBConnection;
import application.Main;
import application.Customer.CustomerProductUI.RedirectionFrom;
import application.DTO.Buyer;
import application.DTO.CurrentProduct;
import application.DTO.CurrentUser;
import application.DTO.Product;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CustomerSearchResultUI {
	
	private Product.PRODUCT_INFO[] allProducts;
	private GridPane productGrid;
	private TextField productSearchText;
	private GridPane suggestionGridPane;
	private Button logoutBtn;
	private Button searchBtn;
	private Button backBtn;
	private ScrollPane sp;
	
	private static Product.PRODUCT_INFO searchedProduct;
	
	private Buyer buyer;
	
	public Scene getScene() {
		Scene s = null;
		try {			
			Parent root = FXMLLoader.load(getClass().getResource("CustomerSearchResultUI.fxml"));
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
			
			AggregateIterable<Document> productDocs = this.buyer.getSearchedProduct(searchedProduct.getId());
			
			this.productSearchText = (TextField) root.lookup("#searchTextField");
			this.suggestionGridPane = (GridPane)root.lookup("#suggestionGridPane");
			this.logoutBtn = (Button) root.lookup("#logoutBtn");
			this.backBtn = (Button) root.lookup("#backBtn");
			this.searchBtn = (Button) root.lookup("#searchBtn");
			StackPane stackPane = (StackPane) root.lookup("#stackPane");
			
			HBox searchAlignment = new HBox();
			searchAlignment.setAlignment(Pos.TOP_CENTER);
					
			this.sp = new ScrollPane();
			stackPane.getChildren().add(sp);
			this.sp.requestFocus();
			StackPane.setMargin(sp, new Insets(50, 0, 0, 0));
			
			productGrid = new GridPane();
			searchAlignment.getChildren().add(productGrid);
			this.sp.setContent(searchAlignment);
			this.sp.setFitToWidth(true);
			
			this.logoutBtn.setOnAction(e -> {
				Main.logout();
			});
			
			this.searchBtn.setOnAction(e -> {
				CustomerMainUI.search(productSearchText, Product.PRODUCT_INFO.values());
			});
			
			this.backBtn.setOnAction(e -> {
				Main.setCustomerMainScene();
			});
			
			this.allProducts = Product.PRODUCT_INFO.values();
			
			this.productSearchText.textProperty().addListener((obs, old, newText) -> {
				if(newText.isEmpty()) {
					this.suggestionGridPane.getChildren().clear();
					this.sp.toFront();
				} else {
					populateSearchSuggestion(newText);
				}
			});
			
			renderSearchResults(productDocs);
			
			s = new Scene(root,1280,720);
			s.getStylesheets().add(getClass().getResource("CustomerMainUI.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return s;
	}
	
	private void populateSearchSuggestion(String searchStr) {
		
		ArrayList<String> suggestionList = new ArrayList<>();
		
		for(Product.PRODUCT_INFO prod : allProducts) {
			if(prod.getName().toLowerCase().contains(searchStr.toLowerCase())) {
				suggestionList.add(prod.getName());
			}
		}
		
		suggestionGridPane.getChildren().clear();
		
		if(suggestionList.size() > 0) {
			sp.toBack();
			
			int gridIndex = 0;
			for(String str : suggestionList) {
				suggestionGridPane.add(createSuggestionLabel(str), 0, gridIndex);
				gridIndex++;
			}	
		} else {
			sp.toFront();
		}
	}
	
	private Label createSuggestionLabel(String text) {
		Label label = new Label(text);
		label.getStyleClass().add("suggestionText");
		label.setMaxWidth(Double.MAX_VALUE);
		label.setPadding(new Insets(10,10,10,2));
		label.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));
		label.setStyle("-fx-font: 15 arial;");
		
		label.setOnMouseClicked(event -> {
			productSearchText.setText(label.getText());
			suggestionGridPane.getChildren().clear();
			sp.toFront();
		});
		
		return label;
	}
	
	public static void setSearchedProduct(Product.PRODUCT_INFO product) {
		searchedProduct = product;
	}
	
	private void renderSearchResults(AggregateIterable<Document> productDocs) {
		
		ArrayList<SearchProductCard> searchProductCardList = new ArrayList<>();
		
		int index = 0;
		for(Document prod : productDocs) {
			
			Document parsedProd = (Document) prod.get("inventory");
			ObjectId productObjectId = parsedProd.getObjectId("_id");
			int productId = (int)parsedProd.get("id");
			int productPrice = (int) parsedProd.get("price");
			int productAmountLeft = (int) parsedProd.get("stock");
			double productRating = (double) parsedProd.get("rating");
			
			SearchProductCard pc = new SearchProductCard(productObjectId, productId, productPrice, productRating, productAmountLeft);
			searchProductCardList.add(pc);
			productGrid.add(pc, 0, index);
			index ++;
		}
	}
}

class SearchProductCard extends HBox {
	
	private ObjectId productObjectId;
	private int productId;
	private int productPrice;
	private double productRating;
	private int productAmountLeft;
	
	Product.PRODUCT_INFO[] allProducts;
	
	public SearchProductCard(ObjectId productObjectId, int productId, int productPrice, double productRating, int productAmountLeft) {
		super(5);
		this.productObjectId = productObjectId;
		this.productId = productId;
		this.productPrice = productPrice;
		this.allProducts = Product.PRODUCT_INFO.values();
		
		setId("productCard");
		
		this.setOnMouseClicked(e -> {
			CurrentProduct.setProduct(this.productObjectId);
			CustomerProductUI.setRedirectFrom(RedirectionFrom.SEARCHPAGE);
			Main.setCustomerProductScene();
		});
		
		Product.PRODUCT_INFO productInfo = null;
		
		for(Product.PRODUCT_INFO pInfo : allProducts) {
			if(pInfo.getId() == this.productId) {
				productInfo = pInfo;
				break;
			}
		}
		
		Image img = new Image(productInfo.getImagePath());
		ImageView iv = new ImageView(img);
		iv.setPreserveRatio(true);
		iv.setFitWidth(300);
		
		VBox content = new VBox(20);
		
		Text productNameText = new Text(productInfo.getName());
		productNameText.setStyle("-fx-font: 20 arial;");
		
		Text productRatingText = new Text("Rating : " + String.format("%.1f", this.productRating) + "/5");
		productRatingText.setStyle("-fx-font: 20 arial;");
		
		Text productAmountLeftText = new Text("Amount left : " + String.valueOf(this.productAmountLeft));
		productAmountLeftText.setStyle("-fx-font: 20 arial;");
		
		Text productPriceText = new Text("price : $" + String.valueOf(this.productPrice));
		productPriceText.setFill(Color.GREEN);
		productPriceText.setStyle("-fx-font: 20 arial;");
		
		VBox space = new VBox();      
		VBox.setVgrow(space, Priority.ALWAYS);
		
		content.getChildren().addAll(productNameText, space, productRatingText, productAmountLeftText, productPriceText);
		
		getChildren().addAll(iv, content);
		HBox.setMargin(content, new Insets(0, 0, 0, 50));
		content.setPadding(new Insets(5, 50, 10, 50));
	}	
}
