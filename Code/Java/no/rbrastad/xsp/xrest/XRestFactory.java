package no.rbrastad.xsp.xrest;

import java.util.Map;

public class XRestFactory extends RestFactory{
		 
	public XRestFactory() {
		super();
	}
	
	public void doService(){
		String pathInfo = getPahInfo();
		Map<String,String> params = request.getParameterMap();
		 
		String jsonString = null;		
		if( pathInfo.equals("helloworld") )
			jsonString = actions.doHelloWorld();
		else
			jsonString = actions.getResponseUnknownAction(pathInfo);
						
		writeJSonResponse(jsonString);
	}

	
	
}
