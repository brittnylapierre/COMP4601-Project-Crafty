package comp4601.project.resources;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import comp4601.project.dao.ProductService;
import comp4601.project.models.Product;
import sun.misc.BASE64Encoder;

@Path("/api")
public class Server {
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
		@Produces(MediaType.TEXT_HTML)
		@Path("query/{terms}")
		public String queryProducts(@PathParam("terms") String terms){
			ProductService p = new ProductService();
			ArrayList<Product> results = p.query(terms);
			String list = "";
			for(Product product : results){
				list += "<h3><a href='"+product.getUrl()+"' target='_blank'>"+product.getTitle()+"</a></h3>";
				list += "<p>Store: " + product.getStore() + "</p>";
				list += "<p><b>$" + product.getPrice() + "</b></p>";
			}
			return "<html><head></head><body><h1>Welcome to Crafty</h1>" + list +"</body></html>";
		}
		
		//TODO:Post return json
		
}
