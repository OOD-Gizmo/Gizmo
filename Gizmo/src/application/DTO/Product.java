package application.DTO;

public class Product {
	
	public static enum PRODUCT_INFO {
		IPHONE12 (1, "IPHONE 12", "iphone_12.jpg", "64GB, Black \nFully Unlocked"),
		MACBOOK_AIR (2, "MACBOOK AIR", "macbook_air.jpg", "M2 chip: 15.3-inch Liquid Retina Display\n8GB Unified Memory, 256GB SSD Storage, 1080p FaceTime HD Camera, Touch ID.\nWorks with iPhone/iPad; Silver"),
		LENOVO_LEGION (3, "Lenovo Legion Pro", "lenovo.jpg", "Intel Core i9-13900HX\nNVIDIA GeForce RTX 4090\n32GB RAM, 2TB (1TB+1TB) NVMe SSD"),
		SONY_XM4 (4, "Sony WH-1000XM4", "sony_xm4.jpg", "Wireless Premium Noise Canceling Overhead Headphones with Mic for Phone-Call"),
		PS5 (5, "PlayStation®5", "ps5.jpg","Digital Edition (slim)"),
		AIRPODS_PRO_2 (6, "Airpods Pro 2", "airpods_pro2.jpg", "Wireless Ear Buds with USB-C Charging\nUp to 2X More Active Noise Cancelling Bluetooth Headphones\nTransparency Mode, Adaptive Audio, Personalized Spatial Audio"),
		SAMSUNG_GALAXY_WATCH_6 (7, "SAMSUNG Galaxy Watch 6", "samsung_galaxy_watch_6.jpg", "44mm Bluetooth Smartwatch, Fitness Tracker, Personalized HR Zones\nAdvanced Sleep Coaching, Heart Monitor, BIA Sensor for Health Wellness Insights\n Big Screen, US Version Silver");
		
		
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
	private String sellerId;
	
	public Product(PRODUCT_INFO productInfo, int price, int stock) {
			this.productInfo = productInfo;
			this.price = price;
			this.stock = stock;
			this.rating = 0;
			this.sellerId = "";
	}
	
	public Product(PRODUCT_INFO productInfo, int price, int stock, double rating) {
		this.productInfo = productInfo;
		this.price = price;
		this.stock = stock;
		this.rating = rating;
		this.sellerId = "";
	}
	
	public Product(PRODUCT_INFO productInfo, int price, int stock, double rating, String sellerId) {
		this.productInfo = productInfo;
		this.price = price;
		this.stock = stock;
		this.rating = rating;
		this.sellerId = sellerId;
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
	
	public String getSellerId() {
		return this.sellerId;
	}
}
