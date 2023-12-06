package application.DTO;

public final class CurrentUser {
	
	private static String userId;
	
	private CurrentUser(){}
	
	public static void setUserId(String id) {
		userId = id;
	}
	
	public static String getUserId() {
		return userId;
	}
}
