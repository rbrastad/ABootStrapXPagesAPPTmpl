package no.rbrastad.extlib.servlet;

import java.util.HashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import no.rbrastad.personas.PersonServlet;
import no.rbrastad.whoami.WhoAmIServlet;


import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

   public class ServletFactory implements IServletFactory { 

	   private HashMap<String, Class> servletMap = null;
	   private ComponentModule module; 

	   public void init (ComponentModule module) { 
	       this.module = module; 
	       this.servletMap = new HashMap<String, Class>();
	       
	       mapServlets();
	   } 
	   
	   /**
	    * Servlets map setup path = servlet class
	    */
	   private void mapServlets(){
		   servletMap.put("/whoami", WhoAmIServlet.class);
		   servletMap.put("/person", PersonServlet.class);  
	   }

	   public ServletMatch getServletMatch (String contextPath, String path) 
	       throws ServletException { 
	       try { 
	         //  throw new Exception (); 
	       } 
	       catch (Throwable t) { 
	          // t.printStackTrace (); 
	       }  
	     
	       String servletPath = ""; 
	       path = path.replace("/xsp/service", "");
	     
	       return new ServletMatch (  getServletByPath(path), servletPath, path); 
	       }
	   
	   
	   private Servlet getServletByPath(String path) throws ServletException{
		   Class servletClass = servletMap.get( path );
//		   if not found we assume a wildcard servlet so we do extensive search for servlet
		   if(servletClass == null){
			   for(String key : servletMap.keySet()){
//				   TODO: rewrite to something more elegant and handle deep path wildcards
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
   

}
