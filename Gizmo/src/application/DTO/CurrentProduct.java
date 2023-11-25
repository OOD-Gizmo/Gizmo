package application.DTO;

import org.bson.types.ObjectId;

public final class CurrentProduct {
	
	private static ObjectId sellerId;
	private static int productId;
	private static int productPrice;
	
	private CurrentProduct(){}
	
	public static void setProduct(ObjectId sId, int pId, int pPrice) {
		sellerId = sId;
		productId = pId;
		productPrice = pPrice;
	}
	
	public static ObjectId getSellerId() {
		return sellerId;
	}
	
	public static int getProductId() {
		return productId;
	}
	
	public static int getProductPrice() {
		return productPrice;
	}
}
