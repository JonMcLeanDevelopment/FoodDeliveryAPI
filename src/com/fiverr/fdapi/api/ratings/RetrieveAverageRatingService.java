package com.fiverr.fdapi.api.ratings;

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

@Path("/rating")
public class RetrieveAverageRatingService {
	
	@POST
	@Path("/average")
	@Produces("application/json")
	public Response averageRatings(@FormParam("placeUUID") String uuid) throws SQLException{
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet results = null;
		
		try {
			conn = API.ds.getConnection();
			
			String sql = "SELECT * FROM `places` WHERE `place_uuid`=?";
			prep = conn.prepareStatement(sql);
			
			prep.setString(1, uuid);
			results = prep.executeQuery();
			
			if(results.next() == false) {
				JSONObject noPlace = new JSONObject();
				noPlace.put("code", 441);
				noPlace.put("message", "Place not found");
				return Response.status(441).entity(noPlace.toString()).build();
			}
			
			int placeId = results.getInt("place_id");
			
			sql = "SELECT * FROM `ratings` WHERE `rating_place_id`=?";
			prep = conn.prepareStatement(sql);
			prep.setInt(1, placeId);
			results = prep.executeQuery();
			
			 if(results.next() == false) {
				 JSONObject noRatings = new JSONObject();
				 noRatings.put("code", 442);
				 noRatings.put("message", "No ratings available");
				 return Response.status(442).entity(noRatings.toString()).build();
			 }
			
			 results.beforeFirst();
			 
			 int count = 0;
			 double sum = 0;
			 
			 while(results.next()) {
				 double num = results.getDouble("rating_num");
				 sum += num;
				 count++;
			 }
			 
			 double average = sum / count;
			 
			 JSONObject good = new JSONObject();
			 good.put("code", 200);
			 good.put("average", average);
			 good.put("count", count);
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
