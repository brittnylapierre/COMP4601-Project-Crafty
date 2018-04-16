package comp4601.project.resources;

import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import comp4601.project.dao.ProductService;
import comp4601.project.dao.UserService;
import comp4601.project.models.Product;
import comp4601.project.models.User;
import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.template.JadeTemplate;
import sun.misc.BASE64Encoder;


/*
 * SECURITY TUTORIAL
 * https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
 * 
 * Extracting the token from the request and validating it
 * The client should send the token in the standard HTTP Authorization header of the request. For example:
 * Authorization: Bearer <token-goes-here>
 * */
@Path("/crafty")
public class Server {
	private static final RetentionPolicy RUNTIME = null;
	private static final ElementType TYPE = null;
	private static final ElementType METHOD = null;

	@NameBinding
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})

	public @interface Secured { }
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
		@Context
		UriInfo uriInfo;
		@Context
		Request request;
		

		private String name;
		//TODO:change root
		private String ROOT = "C:/Users/IBM_ADMIN/dev/COMP4601-Project/server";

		//String ROOT=  "/Users/kellymaclauchlan/code/mobile/project/COMP4601-Project/server";
		String path = ROOT + "/public/";
		public Server() {
			name = "Crafty";
		}
		
		@GET
		@Produces({MediaType.TEXT_HTML})
		public String viewHome(){
			JadeTemplate template;
			try {
				template = Jade4J.getTemplate(path + "index.jade");
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("appName", "Crafty");
				return Jade4J.render(template, model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@GET
		@Path("dashboard")
		@Produces({MediaType.TEXT_HTML})
		public String viewDashboard(@CookieParam("token") Cookie cookie){
			JadeTemplate template;
			try {
				template = Jade4J.getTemplate(path + "dashboard.jade");
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("appName", "Crafty");
				return Jade4J.render(template, model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@GET
		@Path("login")
		@Produces({MediaType.TEXT_HTML})
		public String viewLogin(){
			JadeTemplate template;
			try {
				template = Jade4J.getTemplate(path + "login.jade");
				/*
				NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
				UserService u = new UserService();
				HashMap<String, ArrayList<Product>> results = u.getUserWatchedQueryProducts(username);*/
				
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("appName", "Crafty");
				return Jade4J.render(template, model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		@Path("parse")
		public String parseProducts(){
			ProductService p = new ProductService();
			p.readFiles();
			return "Welcome to Crafty";
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("query/{terms}")
		public Response queryProducts(@PathParam("terms") String terms){
			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
			ProductService p = new ProductService();
			ArrayList<Product> results = p.query(terms);
			String list = "[";
			int curr = 0;
			//double price = 2.50000000000003;
			//System.out.println(currencyFormatter.format(price));
			for(Product product : results){
				list += "{";
				list += "\"title\": \"" + product.getTitle() + "\", ";
				list += "\"store\": \"" + product.getStore() + "\", ";
				list += "\"url\": \"" + product.getUrl() + "\", ";
				list += "\"price\": \"" + currencyFormatter.format(product.getPrice()) + "\"";
				list += "}";
				if(curr != results.size()-1){
					list += ",";
				}
				curr++;
			}
			list += "]";
			return Response.ok(list).build();//results;
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("watching")
		public Response getUserWatching(@CookieParam("token") Cookie cookie){
			String token = cookie.getValue();
			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
			UserService u = new UserService();
			User user = u.findUserWithToken(token);
			HashMap<String, ArrayList<Product>> results = u.getUserWatchedQueryProducts(user.getUsername());
			
			String list = "[";
			
			Iterator it = results.entrySet().iterator();
	        int curr = 0;
		    while (it.hasNext()) {
		        Map.Entry<String,ArrayList<Product>> pair = (Map.Entry<String,ArrayList<Product>>) it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        int currProd = 0;
		        list += "{\"title\":\""+pair.getKey()+"\" , \"topProducts\": [";
		        ArrayList<Product> prods = pair.getValue();
		        for(Product product: prods){
			        list += "{";
					list += "\"title\": \"" + product.getTitle() + "\", ";
					list += "\"store\": \"" + product.getStore() + "\", ";
					list += "\"url\": \"" + product.getUrl() + "\", ";
					list += "\"price\": \"" + currencyFormatter.format(product.getPrice())  + "\"";
					list += "}";
					if(currProd != prods.size()-1){
						list += ",";
					}
					currProd++;
		        }
		        list += "]}";
		        if(curr != results.size()-1){
					list += ",";
				}
		        curr++;
		    }
	        list += "]";
			return Response.ok(list).build();//results;
		}
		
		@POST
		@Path("add-product")
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		@Produces(MediaType.APPLICATION_JSON)
		public Response addProduct(@FormParam("title") String title, 
				@FormParam("store") String store,
				@FormParam("url") String url,
				@FormParam("brand") String brand,
				@FormParam("price") double price){
			ProductService p = new ProductService();
			Product newProd = new Product(title, store, url, price);
			newProd.setBrand(brand);
			p.addProductToDB(newProd);
			return Response.ok("{\"success\": true}").build();
		}
		
		
		//@Secured
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		@Path("watch")
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		public Response watchQuery(@CookieParam("token") Cookie cookie,@FormParam("terms") String terms){
			String token = cookie.getValue();
			UserService u = new UserService();
			User user = u.findUserWithToken(token);
			String username = user.getUsername();
			u.watchQuery(username, terms);
			return Response.ok("{\"success\": true}").build();
		}
		
		//@Secured
		@DELETE
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		@Path("unwatch")
		public Response removeWatchQuery(@CookieParam("token") Cookie cookie, @FormParam("terms") String terms){
			String token = cookie.getValue();
			UserService u = new UserService();
			User user = u.findUserWithToken(token);
			String username = user.getUsername();
			u.removeWatchQuery(username, terms);
			return Response.ok("{\"success\": true}").build();
		}
		
		//@Secured
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		@Path("viewed")
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		public Response addViewed(@CookieParam("token") Cookie cookie, @FormParam("product") String product){
			String token = cookie.getValue();
			UserService u = new UserService();
			User user = u.findUserWithToken(token);
			String username = user.getUsername();
			u.addViewedProduct(username, product);
			return Response.ok("{\"success\": true}").build();
		}
		
		@POST
		@Path("create-account")
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		@Produces(MediaType.APPLICATION_JSON)
		public Response createUser(@FormParam("username") String username, 
				@FormParam("password") String password){
			UserService u = new UserService();
			boolean success = u.createUser(username, password);
			return Response.ok("{\"success\":"+success+"}").build();
		}
		
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		@Path("generateUsers")
		public String generateUsers(){
			ProductService p = new ProductService();
			UserService u = new UserService();
			String s = "user 0's items are:\n";
			for(int i=0;i<500;i++){//create 500 users with 100 random products each time is avout 1 min / 100 prodducts
				String name="name"+i;
				u.createUser(name, "pass");
				User use=u.findOne(name, "pass");
				ArrayList<Product>products= p.getRandomProducts(100);//get 100 random products
				for(Product prod : products){
					u.addViewedProduct(name, prod.getTitle());
					if(i==0){
					s+=prod.getTitle()+'\n';
					}//TODO add to other places where items are viewed
					switch(prod.getStore()){
						case "Wallacks":
							use.wallack++;
							break;
						case "Jerrys":
							use.jerrys++;
							break;
						case "Above Ground Art Supplies":
							use.aboveground++;
							break;
						case "Deserres":
							use.deserres++;
							break;
						case "Art Shack":
							use.artshack++;
							break;		
					}
					//adding to price vars. 
					double price = prod.getPrice();
					if(price<20){
						use.oneTwenty++;
						
					}else if(price<50){
						use.twentyFifty++;
						
					}else if(price<100){
						use.fiftyHunderd++;
					}else if(price<300){
						use.hundredThree++;
					}else{
						use.overThree++;
					}
					
						
				}
				u.updateUser(name, use);
			}
			return "Welcome to Crafty\n"+s;
		}
		
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		@Path("generateCommunities")
		public String generateCommunitites(){
			ProductService p = new ProductService();
			UserService u = new UserService();
			String s = "user 0's items are:\n";
			
			try {
				ArrayList<User> users = u.getAllUsers();
				Communitizer community = new Communitizer(users.size());
				int i=0;
				for(User use :users){
					community.addUser(use, i);
					i++;
				}
				community.algorithm();
				 i=0;
				for(User use :users){
					use.cluster=community.getCommuityForUser(i);
					i++;
					u.updateUser(use.getUsername(), use);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "Users are now in communities\n"+s;
		}
		
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("recomended/{user}")
		public Response recommendProducts(@CookieParam("token") Cookie cookie){
			NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
			ProductService p = new ProductService();
			UserService u = new UserService();
			String token = cookie.getValue();
			User use = u.findUserWithToken(token);
			
			String store= use.getMostShoppedAt();
			String price=use.getMostSpent();
//			store="Deserres";
//			price="two";
			ArrayList<Product> results = p.recomended(store, price);
			String list = "[";
			int curr = 0;
			//double price = 2.50000000000003;
			//System.out.println(currencyFormatter.format(price));
			for(Product product : results){
				list += "{";
				list += "\"title\": \"" + product.getTitle() + "\", ";
				list += "\"store\": \"" + product.getStore() + "\", ";
				list += "\"url\": \"" + product.getUrl() + "\", ";
				list += "\"price\": \"" + currencyFormatter.format(product.getPrice()) + "\"";
				list += "}";
				if(curr != results.size()-1){
					list += ",";
				}
				curr++;
			}
			list += "]";
			return Response.ok(list).build();//results;
		}
}
