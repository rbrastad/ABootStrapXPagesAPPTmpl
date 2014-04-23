package no.rbrastad.util;

import com.ibm.commons.util.io.json.JsonJavaObject;

public class JSonUtil {

	public String getJSONResponseByFileNameStatusMessage(String action, String fileName, boolean status, String responseMessage){
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("action", action);
		returnJSON.put("status", status );
		returnJSON.put("filename", fileName );
		returnJSON.put("message", responseMessage );
		
		return returnJSON.toString();
	}
	
	
	public String getJSONResponseByFileNameStatusMessage(String action, String fileName, boolean status, String responseMessage, long linjer){
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("action", action);
		returnJSON.put("status", status );
		returnJSON.put("filename", fileName );
		returnJSON.put("message", responseMessage );
		returnJSON.put("lines", Long.toString( linjer ) );
		
		return returnJSON.toString();
	}

}
