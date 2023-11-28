package application.Customer;

import static com.mongodb.client.model.Filters.eq;

import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Aggregates.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;

import application.DBConnection;
import application.Main;
import application.DTO.CurrentUser;
import application.DTO.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class CustomerMainUI {
	
	static Product.PRODUCT_INFO[] allProducts;
	ArrayList<ProductCard> allProductCards = new ArrayList<ProductCard>();
	GridPane productGrid;
	
	static TextField productSearchText;
	GridPane suggestionGridPane;
	Button logoutBtn;
	Button searchBtn;
	
	static final int GRID_ROW = 2;
	static final int GRID_COLUMN = 4;
	
	static CustomerSearchResultUI searchResultUI;
	
	public Scene getScene() {
		Scene s = null;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("CustomerMainUI.fxml"));
			Document doc = DBConnection.getCollection("Users").find(eq("_id", CurrentUser.getUserId())).first();
			
			productGrid = (GridPane) root.lookup("#productGrid");
			productSearchText = (TextField) root.lookup("#searchTextField");
			suggestionGridPane = (GridPane)root.lookup("#suggestionGridPane");
			logoutBtn = (Button) root.lookup("#logoutBtn");
			searchBtn = (Button) root.lookup("#searchBtn");
			
			allProducts = Product.PRODUCT_INFO.values();
			
			logoutBtn.setOnAction(e -> {
				Main.logout();
			});
			
			searchBtn.setOnAction(e -> {
				search(productSearchText, allProducts);
			});
						
			productSearchText.textProperty().addListener((obs, old, newText) -> {
				if(newText.isEmpty()) {
					suggestionGridPane.getChildren().clear();
				} else {
					populateSearchSuggestion(newText);
				}
			});
			
									
			AggregateIterable<Document> inventoryDocs = DBConnection.getCollection("Inventory").aggregate(Arrays.asList(Aggregates.match(gt("inventory.stock", 0)), Aggregates.sample(8)));

			for(Document inv : inventoryDocs) {
				ArrayList<ProductCard> pCards = getProductCards(inv);
				allProductCards.addAll(pCards);
			}
			
			int cardIndex = 0;
			for(int i = 0; i < GRID_ROW; i++) {
				for(int j = 0; j < GRID_COLUMN; j++) {
					if(cardIndex < allProductCards.size()) {
						productGrid.add(allProductCards.get(cardIndex), j, i);
						cardIndex++;
					} else {
						break;
					}
				}
			}
			
			
			if(doc == null) {
				return s;
			}
			
			s = new Scene(root,1280,720);
			s.getStylesheets().add(getClass().getResource("CustomerMainUI.css").toExternalForm());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return s;
	}
	
	public static void search(TextField productSearchText, Product.PRODUCT_INFO[] allProducts) {
		String searchedText = productSearchText.getText();
		boolean isFound = false;
		if(!searchedText.isEmpty()) {
			for(Product.PRODUCT_INFO prod : allProducts) {
				if(prod.getName().equals(searchedText)) {
					isFound = true;
					CustomerSearchResultUI.setSearchedProduct(prod);
					Main.setCustomerSearchScene();
					break;
				}
			}
			
			if(!isFound) {
				// TODO: show error;
			}
		}
	}
	
	ArrayList<ProductCard> getProductCards(Document invDoc) {
		List<Document> inventoryList = invDoc.getList("inventory", Document.class);
		
		ArrayList<ProductCard> productCards = new ArrayList<>();
		
		for(Document inv : inventoryList) {
			ObjectId productObjectId = inv.getObjectId("_id");
			int productId = (int)inv.get("id");
			int productPrice = (int) inv.get("price");
			
			ProductCard pc = new ProductCard(productObjectId, productId, productPrice);
			productCards.add(pc);
		}
		
		return productCards;
	}
	
	void populateSearchSuggestion(String searchStr) {
		
		Pattern pattern = Pattern.compile(searchStr, Pattern.CASE_INSENSITIVE);
		
		ArrayList<String> suggestionList = new ArrayList<>();
		
		for(Product.PRODUCT_INFO prod : allProducts) {
			Matcher matcher = pattern.matcher(prod.getName());
			if(matcher.find()) {
				suggestionList.add(prod.getName());
			}
		}
		
		if(suggestionList.size() < 0) {
			suggestionList.clear();
		} else {
			int gridIndex = 0;
			for(String str : suggestionList) {
				suggestionGridPane.add(createSuggestionLabel(str), 0, gridIndex);
				gridIndex++;
			}
		}
	}
	
	Label createSuggestionLabel(String text) {
		Label label = new Label(text);
		label.getStyleClass().add("suggestionText");
		label.setMaxWidth(Double.MAX_VALUE);
		label.setPadding(new Insets(10,10,10,2));
		label.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));
		label.setStyle("-fx-font: 15 arial;");
		
		label.setOnMouseClicked(event -> {
			productSearchText.setText(label.getText());
			suggestionGridPane.getChildren().clear();
		});
		
		return label;
	}
}

class ProductCard extends VBox {
	
	private ObjectId productObjectId;
	private int productId;
	private int productPrice;
	
	Product.PRODUCT_INFO[] allProducts;
	
	public ProductCard(ObjectId productObjectId, int productId, int productPrice) {
		super(5);
		this.productObjectId = productObjectId;
		this.productId = productId;
		this.productPrice = productPrice;
		this.allProducts = Product.PRODUCT_INFO.values();
		
		setId("productCard");
		
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
		
		HBox content = new HBox();
		
		Text productNameText = new Text(productInfo.getName());
		productNameText.setStyle("-fx-font: 20 arial;");
		
		Text productPriceText = new Text("$" + String.valueOf(this.productPrice));
		productPriceText.setFill(Color.GREEN);
		productPriceText.setStyle("-fx-font: 20 arial;");
		
		HBox space = new HBox();      
	    HBox.setHgrow(space, Priority.ALWAYS);
		
		content.getChildren().addAll(productNameText, space, productPriceText);
		
		getChildren().addAll(iv, content);
	}	
}
