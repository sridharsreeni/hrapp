package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.core.Saying;
import com.google.common.base.Optional;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sridhar on 12/19/2014.
 */
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
	
	private static final String
		DEFAULT_INDUSTRY_NAME = "software",
		DEFAULT_LOCATION_NAME = "california";
	
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Path("/hello")
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.or(defaultName));
        return new Saying(counter.incrementAndGet(), value);
    }
    
    @GET
    @Path("/hrapp")
    @Timed
    public String getCompanyInfo(@QueryParam("industry") Optional<String> industryName,
    		@QueryParam("location") Optional<String> locationName) {
    	Client client = Client.create();
    	WebResource webResource = client.resource("http://api.glassdoor.com/api/api.htm");
    	String industryNameParam = industryName.or(DEFAULT_INDUSTRY_NAME);
    	String locationNameParam = locationName.or(DEFAULT_LOCATION_NAME);
    	MultivaluedMap queryParams = new MultivaluedMapImpl();
    	queryParams.add("v", "1");
    	queryParams.add("format", "json");	
    	queryParams.add("t.p", "28567");
    	queryParams.add("t.k", "e1cZdkaWBCi");
    	queryParams.add("action", "employers");
    	queryParams.add("q", industryNameParam);
    	queryParams.add("l", locationNameParam);
    	queryParams.add("userip", "120.0.0.1");
    	queryParams.add("useragent", "Mozilla/%2F4.0");
    	
    	return webResource.queryParams(queryParams).get(String.class);
    }
}