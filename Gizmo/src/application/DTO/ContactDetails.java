package application.DTO;


public class ContactDetails {
	private String mobileNumber;
	private int pinCode;
	private String streetAddress;
	private String city;
	private String state;
	
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public int getPinCode() {
		return pinCode;
	}
	public void setPinCode(int pinCode) {
		this.pinCode = pinCode;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
	public ContactDetails(String mobileNumber, int pinCode, String streetAddress, String city, String state) {
		super();
		this.mobileNumber = mobileNumber;
		this.pinCode = pinCode;
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = state;
	}
	
	
}
