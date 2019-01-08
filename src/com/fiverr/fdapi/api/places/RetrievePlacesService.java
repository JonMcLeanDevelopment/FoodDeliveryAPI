package com.fiverr.fdapi.api.places;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fiverr.fdapi.API;
import com.fiverr.fdapi.helper.LocationHelper;
import com.fiverr.fdapi.models.Place;

@Path("/places")
public class RetrievePlacesService {
	
	private static final double range = 20.0;
	
	@POST
	@Path("/retrieve")
	@Produces("application/json")
	public Response retrieve(@FormParam("latitude") double latitude, @FormParam("longitude") double longitude, @FormParam("countryCode") String countryCode, @FormParam("city") String city) throws SQLException {
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet results = null;
		
		try {
			conn = API.ds.getConnection();
			String sql = "SELECT * FROM `places` WHERE `place_country_code`=? AND `place_city`=?";
			prep = conn.prepareStatement(sql);
			
			prep.setString(1, countryCode);
			prep.setString(2, city);
			results = prep.executeQuery();
			
			if(results.next() == false) {
				JSONObject noResults = new JSONObject();
				noResults.put("code", 421);
				noResults.put("message", "No places found in that city and country");
				return Response.status(421).entity(noResults.toString()).build();
			}
			
			ArrayList<Place> places = new ArrayList<>();
			
			results.beforeFirst();
			while(results.next()) {
				Place place = new Place(results.getString("place_name"), results.getDouble("place_latitude"), results.getDouble("place_longitude"), results.getString("place_country_code"), results.getString("place_city"), results.getString("place_uuid"));
				places.add(place);
			}
			
			JSONArray validPlaces = new JSONArray();
			
			for(Place place : places) {
				double lat = place.getLatitude();
				double lon = place.getLongitude();
				
				boolean isWithinRange = LocationHelper.isCoordinateInRange(range, latitude, longitude, lat, lon);
				double distance = LocationHelper.getDistance(latitude, longitude, lat, lon);
				System.out.println(place.getName() + ": "  + distance + "(" + lat + ", " + lon + "), (" + latitude + ", " + longitude + ")");
				if(isWithinRange) {
					JSONObject placeJSON = new JSONObject();
					placeJSON.put("name", place.getName());
					placeJSON.put("uuid", place.getUniqueId());
					placeJSON.put("latitude", lat);
					placeJSON.put("longitude", lon);
					placeJSON.put("distance", distance);
					
					validPlaces.put(placeJSON);
				}
			}
			
			return Response.ok().entity(validPlaces.toString()).build();
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
