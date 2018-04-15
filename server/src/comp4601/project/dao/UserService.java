package comp4601.project.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import comp4601.project.models.Product;
import comp4601.project.models.User;

public class UserService {
	MongoClient mongoClient;
	DB database;
	DBCollection userCollection;

	int refresh = 300000;
	
	public UserService(){
		try {
			mongoClient = new MongoClient("localhost", 27017);
			database = mongoClient.getDB("craftyDB");
			userCollection = database.getCollection("users");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean createUser(String username, String password){
		//craftyDB products
		try {
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("username", username);
			newDocument.put("password", password);
			userCollection.insert(newDocument);
			return true;
		} catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("error creating user");
			return false;
		}
	}
	
	public User findOne(String username, String password){
		//System.out.println("in db fn");
		BasicDBObject query = new BasicDBObject();
		//System.out.println("in db fn 2");
		query.put("username", username);
		//System.out.println("in db fn 3");
		query.put("password", password);
		//System.out.println("in db fn 4");
		BasicDBObject result = (BasicDBObject) userCollection.findOne(query);	
		//System.out.println("in db fn 5");
		if(!result.isEmpty()){
			//System.out.println("user 1!");
			String usernameRes = result.getString("username");
			String token = "";
			Long accessTimeMS = (long) 0;
			if(result.containsField("token")){
				token = result.getString("token");
			}
			if(result.containsField("accessTimeMS")){
				accessTimeMS = result.getLong("accessTimeMS");
			}
			//System.out.println("user 2!");
			return new User(usernameRes, token, accessTimeMS);		
		}
		//System.out.println("no user :-(");
		return null;
	}
	
	public User checkToken(String token){
		BasicDBObject query = new BasicDBObject();
		query.put("token", token);
		BasicDBObject result = (BasicDBObject) userCollection.findOne(query);	
		if(!result.isEmpty()){
			String usernameRes = result.getString("username");
			Long accessTimeMS = result.getLong("accessTimeMS");
			Long max = accessTimeMS + refresh;
			Long currTime = System.currentTimeMillis();
			if(max < accessTimeMS){
				return null;
			}
			return new User(usernameRes, token, accessTimeMS);		
		}
		return null;
	}
	
	public User updateToken(String username, String token){
		User u = new User(username, token);
		BasicDBObject newDocument = new BasicDBObject("$set", new BasicDBObject().append("token", token));
		BasicDBObject newDocument2 = new BasicDBObject("$set", new BasicDBObject().append("accessTimeMS", u.getAccessTimeMS()));
		BasicDBObject searchQuery = new BasicDBObject().append("username", username);
		userCollection.update(searchQuery, newDocument);
		userCollection.update(searchQuery, newDocument2);
		return u;
	}
	
	public void watchQuery(String username, String watchQuery){
		 DBObject listItem = new BasicDBObject("watching", watchQuery);
		 DBObject updateQuery = new BasicDBObject("$push", listItem);
		 userCollection.update(new BasicDBObject("username", username), updateQuery);
	}
	
	
	public void removeWatchQuery(String username, String watchQuery){
		 DBObject listItem = new BasicDBObject("watching", watchQuery);
		 DBObject updateQuery = new BasicDBObject("$pull", listItem);
		 userCollection.update(new BasicDBObject("username", username), updateQuery);
	}
	

	public void addViewedProduct(String username, String product){
		 DBObject listItem = new BasicDBObject("viewed", product);
		 DBObject updateQuery = new BasicDBObject("$push", listItem);
		 userCollection.update(new BasicDBObject("username", username), updateQuery);
	}
	
	public HashMap<String, ArrayList<Product>> getUserWatchedQueryProducts(String username){
		HashMap<String, ArrayList<Product>> results = new HashMap<String, ArrayList<Product>>();
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		BasicDBObject result = (BasicDBObject) userCollection.findOne(query);
		if(result.containsField("watching")){
			ProductService p = new ProductService();
			BasicDBList list = (BasicDBList) result.get("watching");
			ArrayList<Product> res = new ArrayList<Product>();
			for(Object el: list) {
				String terms = (String) el;
				ArrayList<Product> topProds = p.queryTop(terms, 5);
				results.put(terms, topProds);
			}
		}
		return results;	
	}
}
