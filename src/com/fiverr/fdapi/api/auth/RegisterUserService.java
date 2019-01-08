package com.fiverr.fdapi.api.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.fiverr.fdapi.API;
import com.fiverr.fdapi.helper.HashHelper;

@Path("/auth")
public class RegisterUserService {
	
	@POST
	@Path("/user/register")
	@Produces("application/json")
	public Response registerUser(@FormParam("phone") String phoneNumber, @FormParam("fullName") String fullName, @FormParam("email") String email, @FormParam("password") String password) throws SQLException {
		
		Connection conn = null;
		PreparedStatement prep = null;
		
		String salt = UUID.randomUUID().toString();
		String passwordHash = HashHelper.sha256(password+salt);
		
		try {
			conn = API.ds.getConnection();
			String sql = "INSERT INTO `users` (`user_phone`, `user_full_name`, `user_pass`, `user_salt`, `user_email`) VALUES (?,?,?,?,?)";
			prep = conn.prepareStatement(sql);
			prep.setString(1, phoneNumber);
			prep.setString(2, fullName);
			prep.setString(3, passwordHash);
			prep.setString(4, salt);
			prep.setString(5, email);
			
			prep.execute();
			
		}catch(SQLException e) {
			
			JSONObject error = new JSONObject();
			error.put("code", 500);
			error.put("error_code", e.getErrorCode());
			error.put("error", e.getMessage());
			
			e.printStackTrace();
			
			return Response.serverError().entity(error.toString()).build();
		}finally {
			if(prep != null) prep.close();
			if(conn != null) conn.close();
		}
		
		JSONObject good = new JSONObject();
		good.put("code", 200);
		good.put("message", "Registration of user was successful");
		return Response.ok().entity(good.toString()).build();
	}
	
}
