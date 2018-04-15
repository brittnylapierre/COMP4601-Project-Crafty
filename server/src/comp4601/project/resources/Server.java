package comp4601.project.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
import sun.misc.BASE64Encoder;


/*
 * SECURITY TUTORIAL
 * https://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
 * 
 * Extracting the token from the request and validating it
 * The client should send the token in the standard HTTP Authorization header of the request. For example:
 * Authorization: Bearer <token-goes-here>
 * */
@Path("/api")
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
		//private String ROOT = "C:/Users/IBM_ADMIN/workspace/COMP4601A2/";
		//String ROOT= "/Users/kellymaclauchlan/code/mobile/a2/COMP4601A2/";
		
		public Server() {
			name = "Crafty";
		}

		
		/*@GET
		@Produces(MediaType.APPLICATION_JSON)
		public Product sayJSON() {
			Product p = new Product("Copic marker barium yellow", 6.78);
			return p;
		}*/
		
		@GET
		@Produces(MediaType.TEXT_PLAIN)
		public String sayPlainTextHello(){
			return "Welcome to Crafty";
		}
		
		@GET
		@Produces(MediaType.TEXT_HTML)
		public String sayHTML() {
			return "<html><head></head><body>"+this.name+"</body></html>";
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
			ProductService p = new ProductService();
			ArrayList<Product> results = p.query(terms);
			String list = "[";
			int curr = 0;
			for(Product product : results){
				list += "{";
				list += "title: '" + product.getTitle() + "', ";
				list += "store: '" + product.getStore() + "', ";
				list += "url: '" + product.getUrl() + "', ";
				list += "price: " + product.getPrice() + "";
				list += "}";
				if(curr != results.size()-1){
					list += ",";
				}
				curr++;
			}
			list += "]";
			return Response.ok(list).build();//results;
		}
		
		//TODO:Post return json
		
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
			return Response.ok("{success:1}").build();
		}
		
		
		@Secured
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("watch/{terms}")
		public Response watchQuery(@PathParam("terms") String terms){
			return Response.ok("{success:1}").build();
		}
		
		@POST
		@Path("create-account")
		@Consumes("application/x-www-form-urlencoded;charset=UTF-8") 
		@Produces(MediaType.APPLICATION_JSON)
		public Response createUser(@FormParam("username") String username, 
				@FormParam("password") String password){
			UserService u = new UserService();
			boolean success = u.createUser(username, password);
			return Response.ok("{success:"+success+"}").build();
		}
}
