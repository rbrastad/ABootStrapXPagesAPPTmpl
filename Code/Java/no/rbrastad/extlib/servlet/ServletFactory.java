package no.rbrastad.extlib.servlet;

import no.rbrastad.extlib.servlet.service.ServletFactoryService;
import no.tine.solidum.proxy.ProxyHenvendelseServlet;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;

   public class ServletFactory extends ServletFactoryService { 
	 
	   public void init (ComponentModule module) { 
//		   REST Service path
		   setPathService("/xsp/api");
		   
		   initService(module);
		   
//		   Setup servlet paths to servlet classes
		   setupServletServicePaths();
	   } 
	    
	   /**
	    * Servlets map setup path and servlet class.
	    */
	   private void setupServletServicePaths(){
		   servletMap.put("/henvendelse", ProxyHenvendelseServlet.class);
		  
	   }

}
