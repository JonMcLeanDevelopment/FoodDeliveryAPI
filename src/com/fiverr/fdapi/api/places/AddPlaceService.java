package com.fiverr.fdapi.api.places;

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

@Path("/places")
public class AddPlaceService {

	@POST
	@Path("/add")
	@Produces("application/json")
	public Response addPlace(@FormParam("name") String name,  @FormParam("latitude") double latitude, @FormParam("longitude") double longitude, @FormParam("countryCode") String countryCode, @FormParam("state") String state) throws SQLException {
		Connection conn = null;
		PreparedStatement prep = null;
		
		try {
			conn = API.ds.getConnection();
			String sql = "INSERT INTO `places` (`place_name`, `place_latitude`, `place_longitude`, `place_country_code`, `place_state`, `place_uuid`) VALUES (?,?,?,?,?,?)";
			prep = conn.prepareStatement(sql);
			prep.setString(1, name);
			prep.setDouble(2, latitude);
			prep.setDouble(3, longitude);
			prep.setString(4, countryCode);
			prep.setString(5, state);
			prep.setString(6, UUID.randomUUID().toString());
			
			prep.execute();
			
			JSONObject good = new JSONObject();
			good.put("code", 200);
			good.put("message", "Successfully added place");
			return Response.ok().entity(good.toString()).build();
		}catch(SQLException e) {
			JSONObject errorReturn = new JSONObject();
			errorReturn.put("code", 500);
			errorReturn.put("error_code", e.getErrorCode());
			errorReturn.put("error", e.getMessage());
			
			e.printStackTrace();
			
			return Response.serverError().entity(errorReturn.toString()).build();
		}finally {
			if(prep != null) prep.close();
			if(conn != null) conn.close();
		}
	}
	
}
