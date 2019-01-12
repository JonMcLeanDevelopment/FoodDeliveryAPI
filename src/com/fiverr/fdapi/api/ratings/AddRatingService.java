package com.fiverr.fdapi.api.ratings;

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

@Path("/rating")
public class AddRatingService {
	
	@SuppressWarnings("resource")
	@POST
	@Path("/add")
	@Produces("application/json")
	public Response addRating(@FormParam("placeUUID") String uuid, @FormParam("rating") double rating, @FormParam("sessionToken") String sessionToken) throws SQLException {
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet results = null;
		
		try {
			conn = API.ds.getConnection();
			
			String sql = "SELECT * FROM `sessions` WHERE `session_token`=?";
			prep = conn.prepareStatement(sql);
			prep.setString(1, sessionToken);
			results = prep.executeQuery();
			
			if(results.next() == false) {
				JSONObject noSession = new JSONObject();
				noSession.put("code", 400);
				noSession.put("message", "Session token does not exist");
				return Response.status(400).entity(noSession.toString()).build();
			}
			
			Timestamp timestamp = results.getTimestamp("session_expiry");
			long currentTime = System.currentTimeMillis();
			if(currentTime > timestamp.getTime()) {
				JSONObject sessionExpired = new JSONObject();
				sessionExpired.put("code", 440);
				sessionExpired.put("message", "Session token has expired");
				return Response.status(440).entity(sessionExpired.toString()).build();
			}
			
			int userId = results.getInt("session_user_id");
			
			sql = "SELECT * FROM `places` WHERE `place_uuid`=?";
			prep = conn.prepareStatement(sql);
			prep.setString(1, uuid);
			results = prep.executeQuery();
			
			if(results.next() == false) {
				JSONObject placeNotFound = new JSONObject();
				placeNotFound.put("code", 441);
				placeNotFound.put("message", "Place not found");
				return Response.status(441).entity(placeNotFound.toString()).build();
			}
			
			int placeId = results.getInt("place_id");
			
			sql = "INSERT INTO `ratings` (`rating_num`, `rating_place_id`, `rating_user_id`) VALUES (?,?,?)";
			prep = conn.prepareStatement(sql);
			prep.setDouble(1, rating);
			prep.setInt(2, placeId);
			prep.setInt(3, userId);
			prep.execute();
			
			JSONObject good = new JSONObject();
			good.put("code", 200);
			good.put("message", "Successfully added rating");
			return Response.ok().entity(good.toString()).build();
		}catch(SQLException e) {
			
			JSONObject errorReturn = new JSONObject();
			errorReturn.put("code", 500);
			errorReturn.put("error_code", e.getErrorCode());
			errorReturn.put("error", e.getMessage());
			
			e.printStackTrace();
			
			return Response.serverError().entity(errorReturn.toString()).build();
		}finally {
			if(results != null) results.close();
			if(prep != null) prep.close();
			if(conn != null) conn.close();
		}
	}
	
}
