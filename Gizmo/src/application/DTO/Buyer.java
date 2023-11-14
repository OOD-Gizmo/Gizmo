package application.DTO;

public class Buyer extends User {
	public Buyer(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails) {
		// TODO Auto-generated constructor stub
		super(firstName, lastName, userID, emailID, contactDetails, USER_TYPE.BUYER);
	}

}
