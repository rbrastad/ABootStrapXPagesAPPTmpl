package no.rbrastad.extlib.servlet.service;

import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class ServletFactoryService implements IServletFactory{

	 public ComponentModule module; 
	 public HashMap<String, Class> servletMap = null;
	 private String pathService = "/xsp/service";	
	 
	 public void initService(ComponentModule module){
		 this.module = module; 
	     this.servletMap = new HashMap<String, Class>();
	 }
	 
	 
	 public ServletMatch getServletMatch (String contextPath, String path) throws ServletException { 
     try {
			// throw new Exception ();
		} catch (Throwable t) {
			// t.printStackTrace ();
		}

		String servletPath = "";
		path = path.replace(pathService, "");

		return new ServletMatch (  getServletByPath(path), servletPath, path); 
     }
 
 
 public  Servlet getServletByPath(String path) throws ServletException{
	   Class servletClass = servletMap.get( path );
//	   if not found we assume a wildcard servlet so we do extensive search for servlet
	   if(servletClass == null){
		   for(String key : servletMap.keySet()){
//			   TODO: rewrite to something more elegant and handle deep path wildcards
			   if(key.endsWith("*")){
				   String keyTmp = key.replace("*", "");
				   if( path.startsWith(keyTmp) ){
					   servletClass = servletMap.get(key);
					   break;
				   }
			   }   
		   }
	   }
	   
	   if(servletClass != null)
		   return getWidgetServlet(servletClass);
	   else
		   throw new ServletException("Servlet path not found.");
 }
    
 
 public Servlet getWidgetServlet (Class servletClazz) throws ServletException { 
	     return module.createServlet (servletClazz.getName(), servletClazz.getSimpleName(), null);
	}


	public void init(ComponentModule arg0) {
		
	}


	public String getPathService() {
		return pathService;
	}


	public void setPathService(String pathService) {
		this.pathService = pathService;
	} 

}
