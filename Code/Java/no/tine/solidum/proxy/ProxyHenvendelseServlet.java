package no.tine.solidum.proxy;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.ibm.commons.util.io.json.JsonJavaObject;

import no.rbrastad.extlib.servlet.service.JsonHttpServlet;
import no.tine.solidum.proxy.http.HttpProxy;

public class ProxyHenvendelseServlet extends JsonHttpServlet {
	
		private String uriSolidumHenvendelser = "https://tiger.tine.no/test/solidum/solidumapi.nsf/xsp/api/henvendelse";
		private String accessToken = "421e7f48-0090-11e6-8d22-5e5517507c66";
		
		private static final long serialVersionUID = 1L;	
		
		public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
			proxyRequest(req, res);
		}

		public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
			proxyRequest(req, res);  
		}
		
		private void proxyRequest( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException{	
			setRequestResponse(req,res);
		
			if(accessToken.equals(req.getParameter("token"))){
				JsonJavaObject redirects = getRedirectPages(req); 
				
				HttpProxy httpProxy = new HttpProxy();
				httpProxy.setProxyRequestUri( uriSolidumHenvendelser );
				
				httpProxy.service(req);
				
				if( httpProxy.getReponseStatusCode() == 200){
					if(redirects.get("success") != null){
						res.sendRedirect( redirects.getAsString("success") );			
					}else{
						PrintWriter out = res.getWriter();
						out.print( httpProxy.getResponseStream() );
						out.close();
						
						res.setStatus(HttpStatus.SC_CREATED);
					}
				}else{
					if(redirects.get("error") != null){
						res.sendRedirect( redirects.getAsString("success") );			
					}else{
						PrintWriter out = res.getWriter();
						out.print( httpProxy.getResponseStream() );
						out.close();
						
						res.setStatus(HttpStatus.SC_BAD_REQUEST);
					}
				}	
			}else{
				res.setStatus(HttpStatus.SC_FORBIDDEN);
			}
				
		}
		
		
		private JsonJavaObject getRedirectPages( HttpServletRequest req ){
			JsonJavaObject redirects = new JsonJavaObject();
			redirects.put("success", req.getParameter("redirectSuccess"));
			redirects.put("error", req.getParameter("redirectError"));
			
			return redirects;
		}
			
}

//JsonJavaObject jsonResponse = new JsonJavaObject();

//if( httpProxy.getReponseStatusCode() == 200){
//	try {
//		JsonJavaObject jsonObject = (JsonJavaObject) JsonParser.fromJson(JsonJavaFactory.instanceEx, httpProxy.getResponseStream());
//		jsonResponse.put("data", jsonObject );					
//	} catch (JsonException e) {
//		e.printStackTrace();
//	}
//	
//	JsonJavaObject status = new JsonJavaObject();
//	status.put("code", httpProxy.getReponseStatusCode() );
//	jsonResponse.put("status", status);
//	
//}else{
//	JsonJavaObject error = new JsonJavaObject();
//	error.put("code", httpProxy.getReponseStatusCode() );
//
//	jsonResponse.put("error", error);
//	jsonResponse.put("data", httpProxy.getResponseStream() );			
//}
//
//doResponseWriteJson( jsonResponse );