package no.rbrastad.extlib.servlet.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;

public class JsonHttpServlet extends HttpServlet{
	
	private HttpServletRequest req;
	private HttpServletResponse res;
	
	public void setRequestResponse(HttpServletRequest req, HttpServletResponse res){
		res.setContentType("application/json");
		res.setHeader("Cache-Control", "no-cache");
		
		this.req = req;
		this.res = res;
	}
	
	
	public void doResponseWriteJson(JsonJavaObject jsonJavaObject){
		try {
			PrintWriter out = res.getWriter ();
			out.print( jsonToString(jsonJavaObject) );			
			out.close ();
			
			if(jsonJavaObject.get("status").equals("true"))
				res.setStatus(HttpServletResponse.SC_ACCEPTED);
			else
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	

	public void doResponseStatus(int httpStatus ){
		res.setStatus( httpStatus ); 
	}
	
	
	public JsonJavaObject getResponseJson(String action, boolean status,String message) {
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("action", action );		
		returnJSON.put("status", Boolean.toString(status) );		
		returnJSON.put("message", message );
		
		return returnJSON;
	}
	
	public JsonJavaObject getResponseJson(String action, boolean status,String message,String unid) {
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("action", action );		
		returnJSON.put("status", Boolean.toString(status) );		
		returnJSON.put("message", message );
		returnJSON.put("unid", unid );
		
		return returnJSON;
	}
	

	private String jsonToString(JsonJavaObject returnJSON ){
		try {
			String str = JsonGenerator.toJson(JsonJavaFactory.instanceEx, returnJSON);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
