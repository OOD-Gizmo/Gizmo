package application.DTO;

enum USER_TYPE{
	ADMIN,
	SELER,
	BUYER
}

public abstract class User {
	private String firstName; 
	private String lastName;
	private String userID; 
	private String emailID; 
	private ContactDetails contactDetails;
	private USER_TYPE user_TYPE; 
	
	
	public USER_TYPE getUser_TYPE() {
		return user_TYPE;
	}
	public void setUser_TYPE(USER_TYPE user_TYPE) {
		this.user_TYPE = user_TYPE;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public ContactDetails getContactDetails() {
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}
	
	
	public User(String firstName, String lastName, String userID, String emailID, ContactDetails contactDetails, USER_TYPE user_Type) {
//		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		this.emailID = emailID;
		this.contactDetails = contactDetails;
		this.user_TYPE = user_Type;
	}
}
