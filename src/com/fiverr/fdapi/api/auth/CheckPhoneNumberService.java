package com.fiverr.fdapi.api.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.fiverr.fdapi.API;

@Path("/auth")
public class CheckPhoneNumberService {
	
	@POST
	@Path("/user/check/phone")
	@Produces("application/json")
	public Response checkPhone(@FormParam("phone") String phone) throws SQLException {
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet results = null;
		
		try {
			conn = API.ds.getConnection();
			String sql = "SELECT * FROM `users` WHERE `user_phone`=?";
			prep = conn.prepareStatement(sql);
			prep.setString(1, phone);
			results = prep.executeQuery();
			
			if(results.next() == false) {
				JSONObject noPhone = new JSONObject();
				noPhone.put("code", 306);
				noPhone.put("message", "No phone number was found");
				return Response.status(306).entity(noPhone.toString()).build();
			}
			
			String fullName = results.getString("user_full_name");
			
			JSONObject phoneFound = new JSONObject();
			phoneFound.put("code", 200);
			phoneFound.put("fullName", fullName);
			phoneFound.put("message", "Phone number exists");
			return Response.ok().entity(phoneFound.toString()).build();
			
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
