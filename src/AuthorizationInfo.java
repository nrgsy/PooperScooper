



//TODO make sure setters actually update attribute in database




public class AuthorizationInfo {
	
	private String customerSecret;
	
	private String customerKey;
	
	private String authorizationSecret;
	
	private String authorizationKey;
	
	private boolean isIncubated;
	
	public AuthorizationInfo(
			String customerSecret,
			String customerKey,
			String authorizationSecret,
			String authorizationKey,
			boolean isIncubated) {
		super();
		this.customerSecret = customerSecret;
		this.customerKey = customerKey;
		this.authorizationSecret = authorizationSecret;
		this.authorizationKey = authorizationKey;
		this.isIncubated = isIncubated;
	}

	public String getCustomerSecret() {
		return customerSecret;
	}

	public String getCustomerKey() {
		return customerKey;
	}

	public String getAuthorizationSecret() {
		return authorizationSecret;
	}

	public String getAuthorizationKey() {
		return authorizationKey;
	}

	public synchronized boolean isIncubated() {
		return isIncubated;
	}

	public synchronized void setIncubated(boolean isIncubated) {
		this.isIncubated = isIncubated;
	
		
//TODO update in the actual database
		
		
		
		
	}
}
