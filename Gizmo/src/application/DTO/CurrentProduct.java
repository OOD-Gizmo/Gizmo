package application.DTO;

import org.bson.types.ObjectId;

public final class CurrentProduct {
	
	private static ObjectId productObjectId;
	
	private CurrentProduct(){}
	
	public static void setProduct(ObjectId pId) {
		productObjectId = pId;
	}
	
	public static ObjectId getProductId() {
		return productObjectId;
	}
}
