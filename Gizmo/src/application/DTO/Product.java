package application.DTO;

import java.util.ArrayList;

enum PRODUCT_INFO {
	IPHONE12 (1, "IPHONE 12", "../", "RAM-Xgb\nblabla");
	
	private int id;
	private String name;
	private String imagePath;
	private String specifications;
	
	PRODUCT_INFO(int id, String name, String imagePath, String specifications) {
		this.id = id;
		this.name = name;
		this.imagePath = imagePath;
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

public class Product {
	
	private PRODUCT_INFO productInfo;
	private int price;
	private int stock;
	private ArrayList<Integer> rating;
	
	public Product(PRODUCT_INFO productInfo, String specifications, int price, int stock) {
			this.productInfo = productInfo;
			this.price = price;
			this.stock = stock;
			this.rating = new ArrayList<>();
	}
	
	public PRODUCT_INFO getProductId() { // can access id, name, image, specifications
		return this.productInfo;
	}

	public int getStock() {
		return this.stock;
	}

	public int getPrice() {
		return this.price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}

	public double getRating() {
		int sum = 0;
		for(int i = 0; i < this.rating.size(); i++) {
			sum += this.rating.get(i);
		}
		
		return sum/this.rating.size();
	}

	public void addRating(int rate) {
		this.rating.add(rate);
	}
}
