package application.DTO;

import org.bson.types.ObjectId;

public final class CurrentUser {
	
	private static ObjectId userId;
	
	private CurrentUser(){}
	
	public static void setUserId(ObjectId id) {
		userId = id;
	}
	
	public static ObjectId getUserId() {
		return userId;
	}
}
