package comp4601.project.models;

public class User {
	String username;
	String token;
	long accessTimeMS;
	public int wallack=0;
	public int aboveground=0;
	public int artshack=0;
	public int jerrys=0;
	public int deserres=0;
	public int oneTwenty=0;
	public int twentyFifty=0;
	public int fiftyHunderd=0;
	public int hundredThree=0;
	public int overThree=0;
	public int cluster=0;

	public User(String username, String token) {
		// TODO Auto-generated constructor stub
		this.username = username;
		this.token = token;
		this.accessTimeMS = System.currentTimeMillis();
	}
	
	public User(String username, String token, long accessTimeMS, int wallack, int aboveground, int artshack,
			int jerrys, int deserres, int oneTwenty, int twentyFifty, int fiftyHunderd, int hundredThree,
			int overThree, int cluster) {
		this.username = username;
		this.token = token;
		this.accessTimeMS = accessTimeMS;
		this.wallack = wallack;
		this.aboveground = aboveground;
		this.artshack = artshack;
		this.jerrys = jerrys;
		this.deserres = deserres;
		this.oneTwenty = oneTwenty;
		this.twentyFifty = twentyFifty;
		this.fiftyHunderd = fiftyHunderd;
		this.hundredThree = hundredThree;
		this.overThree = overThree;
		this.cluster=cluster;
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
	public int getNumVisited(){
		int count=0;
		count +=wallack;
		count +=aboveground;
		count +=artshack;
		count +=jerrys;
		count +=deserres;
		return count;
	}
	public String getMostShoppedAt(){
		int max =0;
		String r = "";
		if(this.wallack>max){
			max=wallack;
			r="Wallacks";
		}
		if(this.aboveground>max){
			max=aboveground;
			r="Above Ground Art Supplies";
		}
		if(this.artshack>max){
			max=artshack;
			r="Art Shack";
		}
		if(this.deserres>max){
			max=deserres;
			r="Deserres";
		}
		if(this.jerrys>max){
			max=jerrys;
			r="Jerrys";
		}
		
		return r;
	}
	public String getMostSpent(){
		int max=0;
		String r = "";
		if(this.oneTwenty>max){
			max=oneTwenty;
			r="one";
		}
		if(this.twentyFifty>max){
			max=twentyFifty;
			r="two";
		}
		if(this.fiftyHunderd>max){
			max=fiftyHunderd;
			r="three";
		}
		if(this.hundredThree>max){
			max=hundredThree;
			r="four";
		}
		if(this.overThree>max){
			max=overThree;
			r="five";
		}
		
		return r;
	}

}
