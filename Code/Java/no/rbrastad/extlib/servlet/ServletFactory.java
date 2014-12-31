package no.rbrastad.extlib.servlet;

import no.rbrastad.extlib.servlet.fileupload.AttachmentServlet;
import no.rbrastad.extlib.servlet.service.ServletFactoryService;
import no.rbrastad.personas.CompanyServlet;
import no.rbrastad.personas.PersonServlet;
import no.rbrastad.whoami.WhoAmIServlet;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;

   public class ServletFactory extends ServletFactoryService { 
	 
	   public void init (ComponentModule module) { 
//		   REST Service path
		   setPathService("/xsp/service");
		   
		   initService(module);
		   
//		   Setup servlet paths to servlet classes
		   setupServletServicePaths();
	   } 
	    
	   /**
	    * Servlets map setup path and servlet class.
	    */
	   private void setupServletServicePaths(){
		   servletMap.put("/whoami", WhoAmIServlet.class);
		   servletMap.put("/person", PersonServlet.class);  
		   servletMap.put("/company", CompanyServlet.class);  
		   servletMap.put("/attachment", AttachmentServlet.class); 
	   }

}
