package application.DTO;

public abstract class User {
	
	public enum USER_TYPE{
		UNDEFINED (0),
		ADMIN (1),
		SELLER (2),
		BUYER (3);
		
		private int typeInt;
		
		USER_TYPE(int typeInt) {
			this.typeInt = typeInt;
		}
		
		public int getTypeInt() {
			return this.typeInt;
		}
		
		public static USER_TYPE getEnumFromTypeInt(int typeInt) {
			switch(typeInt) {
				case 1:
					return USER_TYPE.ADMIN;
				case 2:
					return USER_TYPE.SELLER;
				case 3:
					return USER_TYPE.BUYER;
				default:
					return USER_TYPE.UNDEFINED;
			}
		}
	}
	
	protected String firstName; 
	protected String lastName;
	protected String userID; 
	protected String emailID; 
	protected ContactDetails contactDetails;
	protected USER_TYPE user_TYPE; 
	
	
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
