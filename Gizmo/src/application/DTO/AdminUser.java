package application.DTO;

public class AdminUser extends User{
	
	public AdminUser(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.ADMIN);
			}
}