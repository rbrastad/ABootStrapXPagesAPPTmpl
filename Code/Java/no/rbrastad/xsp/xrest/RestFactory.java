package no.rbrastad.xsp.xrest;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class RestFactory {
	
	public FacesContext facesContext = null;
	public ExternalContext externalContext = null;
	public HttpServletResponse  response = null;
	public HttpServletRequest  request = null;
	public Actions actions = null;
	 
	public RestFactory() {
		facesContext = ExtLibUtil.getXspContext().getFacesContext();
		externalContext = facesContext.getExternalContext();
		response = (HttpServletResponse) externalContext.getResponse();
		request = (HttpServletRequest) externalContext.getRequest();
		actions = new Actions();
	}
		
	public String getPahInfo() {
		String pathInfo = request.getPathInfo();
		if( pathInfo.startsWith("/") )
			pathInfo = pathInfo.replaceFirst("/", "");
		return pathInfo;
	}

	public void writeJSonResponse(String jsonString) {
		writeResponseString(jsonString,"application/json");
	}
	
	public void writeTextResponse(String jsonString) {
		writeResponseString(jsonString,"application/text");
	}
	
	public void writeResponseString(String responseString, String contentType) {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		response.setContentType(contentType);
		response.setHeader("Cache-Control", "no-cache");
		
		try {
			writer.write( responseString );
		} catch (Exception e) {
			//e.printStackTrace();
			}
		
		try {
			writer.endDocument();
		} catch (IOException e) {
			//e.printStackTrace();
			}
	}
	
}
