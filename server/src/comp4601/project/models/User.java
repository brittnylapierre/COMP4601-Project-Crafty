package comp4601.project.models;

public class User {
	String username;
	String token;
	long accessTimeMS;

	public User(String username, String token) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.token = token;
		this.accessTimeMS = System.currentTimeMillis();
	}
	
	public User(String username, String token, long accessTimeMS) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.token = token;
		this.accessTimeMS = accessTimeMS;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getAccessTimeMS() {
		return accessTimeMS;
	}

	public void setAccessTimeMS(long accessTimeMS) {
		this.accessTimeMS = accessTimeMS;
	}

}
