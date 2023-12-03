package application.DTO;

public class Product {
	
	public static enum PRODUCT_INFO {
		IPHONE12 (1, "IPHONE 12", "iphone_12.jpg", "RAM-Xgb\nblabla");
		
		private int id;
		private String name;
		private String imagePath;
		private String specifications;
		
		PRODUCT_INFO(int id, String name, String imagePath, String specifications) {
			this.id = id;
			this.name = name;
			this.imagePath = imagePath;
			this.specifications = specifications;
		}
		
		public int getId() {
			return this.id;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getImagePath() {
			return this.imagePath;
		}

		public String getSpecifications() {
			return specifications;
		}
	}
	
	private PRODUCT_INFO productInfo;
	private int price;
	private int stock;
	private double rating;
	
	public Product(PRODUCT_INFO productInfo, int price, int stock) {
			this.productInfo = productInfo;
			this.price = price;
			this.stock = stock;
			this.rating = 0;
	}
	
	public Product(PRODUCT_INFO productInfo, int price, int stock, double rating) {
		this.productInfo = productInfo;
		this.price = price;
		this.stock = stock;
		this.rating = rating;
}
	
	public PRODUCT_INFO getProductInfo() { // can access id, name, image, specifications
		return this.productInfo;
	}

	public int getStock() {
		return this.stock;
	}
	
	public int setStock(int stock) {
		return this.stock = stock;
	}

	public int getPrice() {
		return this.price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}

	public double getRating() {
		return this.rating;
	}
}
