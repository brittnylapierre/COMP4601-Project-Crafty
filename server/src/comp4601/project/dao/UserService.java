package comp4601.project.dao;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import comp4601.project.models.Product;
import comp4601.project.models.Product.Condition;
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
	
	public User createUser(String username, String password){
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		query.put("password", password);
		BasicDBObject result = (BasicDBObject) userCollection.findOne(query);	
		if(!result.isEmpty()){
			String usernameRes = result.getString("username");
			String token = result.getString("token");
			Long accessTimeMS = result.getLong("accessTimeMS");
			return new User(usernameRes, token, accessTimeMS);		
		}
		return null;
	}
	
	public User findOne(String username, String password){
		BasicDBObject query = new BasicDBObject();
		query.put("username", username);
		query.put("password", password);
		BasicDBObject result = (BasicDBObject) userCollection.findOne(query);	
		if(!result.isEmpty()){
			String usernameRes = result.getString("username");
			String token = result.getString("token");
			Long accessTimeMS = result.getLong("accessTimeMS");
			return new User(usernameRes, token, accessTimeMS);		
		}
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
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.append("$set", new BasicDBObject().append("token", token));
		newDocument.append("$set", new BasicDBObject().append("accessTimeMS", u.getAccessTimeMS()));
		BasicDBObject searchQuery = new BasicDBObject().append("username", username);
		userCollection.update(searchQuery, newDocument);
		return u;
	}
}
