package no.rbrastad.xsp.xrest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.commons.util.io.json.JsonJavaObject;

public class Actions {
		
	public String getResponseUnknownAction(String pathInfo) {
		String jsonString;
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("context", pathInfo);
		returnJSON.put("status", "false");
		returnJSON.put("message", "unknown contextpath action");
		
		jsonString = returnJSON.toString();
		return jsonString;
	}

	public String doHelloWorld() {
		
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("message", "Hello World");
		
		return returnJSON.toString();
	}

	public String doPersonasDirectory() {	
		List<HashMap<String, String>> dataList = new ArrayList<HashMap<String,String>>();
		  
		HashMap<String, String> person = new HashMap<String, String>();
		person.put("firstname", "Runar");
		person.put("lastname", "Brastad");
		person.put("company", "Item Consulting AS");
		person.put("email", "runar@item.no");
		
		dataList.add(person);
		
		person = new HashMap<String, String>();
		person.put("firstname", "Terje");
		person.put("lastname", "Bjørkedal");
		person.put("company", "Item Consulting AS");
		person.put("email", "terje@item.no");
		
		dataList.add(person);
		
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("aaData", dataList);
		   
		return returnJSON.toString();
	}
}
