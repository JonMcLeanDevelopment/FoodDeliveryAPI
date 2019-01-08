package com.fiverr.fdapi.api.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.fiverr.fdapi.API;
import com.fiverr.fdapi.helper.HashHelper;

@Path("/auth")
public class LoginUserService {

	@POST
	@Path("/user/login")
	@Produces("application/json")
	public Response login(@FormParam("phone") String phoneNumber, @FormParam("password") String password) throws SQLException {
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet results = null;
		
		try {
			conn = API.ds.getConnection();
			
			String sql = "SELECT * FROM `users` WHERE `user_phone`=?";
			prep = conn.prepareStatement(sql);
			prep.setString(1, phoneNumber);
			results = prep.executeQuery();
			
			if(results.next() == false)  {
				JSONObject noUser = new JSONObject();
				noUser.put("code", 400);
				noUser.put("message", "Invalid login information");
				return Response.status(400).entity(noUser.toString()).build();
			}
			
			int userId = results.getInt("user_id");
			String salt = results.getString("user_salt");
			String storedPassword = results.getString("user_pass");
			
			String input = HashHelper.sha256(password + salt);
			
			if(!input.equals(storedPassword)) {
				JSONObject noUser = new JSONObject();
				noUser.put("code", 400);
				noUser.put("message", "Invalid Password");
				return Response.status(400).entity(noUser.toString()).build();
			}
			
			sql = "INSERT INTO `sessions` (`session_user_id`, `session_expiry`, `session_token`) VALUES (?,?,?)";
			prep = conn.prepareStatement(sql);
			
			
			byte[] sessionTokenBytes = new byte[16];
			API.rand.nextBytes(sessionTokenBytes);
			String sessionToken = HashHelper.byteArrayToString(sessionTokenBytes);
			
			long currentTime = System.currentTimeMillis();
			long expiryTime = currentTime + (604800 * 1000); // current time + 7 days
			Timestamp timestamp = new Timestamp(expiryTime);
			
			prep.setInt(1, userId);
			prep.setTimestamp(2, timestamp);
			prep.setString(3, sessionToken);
			
			prep.execute();
			
			JSONObject good = new JSONObject();
			good.put("code", 200);
			good.put("token", sessionToken);
			good.put("expiry", expiryTime);
			good.put("message", "Successfully logged in");
			return Response.ok().entity(good.toString()).build();
		}catch(SQLException e) {
			JSONObject error = new JSONObject();
			error.put("code", 500);
			error.put("error_code", e.getErrorCode());
			error.put("error", e.getMessage());
			
			e.printStackTrace();
			
			return Response.serverError().entity(error.toString()).build();
		}finally {
			if(results != null) results.close();
			if(prep != null) prep.close();
			if(conn != null) conn.close();
		}
		
	}
	
}
