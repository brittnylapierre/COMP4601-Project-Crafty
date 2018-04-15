package comp4601.project.resources;

import javax.naming.AuthenticationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.RandomStringUtils;

import comp4601.project.dao.UserService;
import comp4601.project.models.User;

@Path("/authentication")
public class AuthenticationEndPoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

        try {

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }      
    }

    private void authenticate(String username, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	UserService u = new UserService();
    	User result = u.findOne(username, password);
    	if(result == null){
    		throw new AuthenticationException();
    	}
    }

    private String issueToken(String username) {
    	String token = RandomStringUtils.randomAlphanumeric(10);
    	UserService u = new UserService();
    	u.updateToken(username,token);
		return token;
    }
}
