package com.fiverr.fdapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/")
public class PingService {

	// When called it responds with the passed in message
	@GET
	@Path("/ping/{message}")
	public Response ping(@PathParam("message") String message) {
		JSONObject json = new JSONObject();
		json.put("ping", message);
		return Response.ok().entity(json.toString()).build();
	}
	
}
