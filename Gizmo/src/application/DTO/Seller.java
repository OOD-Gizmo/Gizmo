package application.DTO;

public class Seller extends User {
	public Seller(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.SELLER);
		
	}

}
