package no.rbrastad.extlib.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import no.rbrastad.extlib.servlet.services.PersonServlet;
import no.rbrastad.extlib.servlet.services.UnknownPathServlet;
import no.rbrastad.extlib.servlet.services.WhoAmIServlet;


import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class ServletFactory implements IServletFactory { 

   private ComponentModule module; 

   public void init (ComponentModule module) { 
       this.module = module; 
   } 

   public ServletMatch getServletMatch (String contextPath, String path) 
       throws ServletException { 
    
       String servletPath = ""; 
       path = path.replace("/xsp/service", "");
       
       if (path.equals ("/whoami"))  
           return new ServletMatch ( getWidgetServlet(WhoAmIServlet.class) , servletPath, path); 
       else if (path.equals ("/person"))  
           return new ServletMatch ( getWidgetServlet(PersonServlet.class) , servletPath, path); 
       else
           return new ServletMatch (getWidgetServlet( UnknownPathServlet.class ), servletPath, path); 
     } 

   public Servlet getWidgetServlet(Class clazz ) throws ServletException { 
	   String SERVLET_WIDGET_CLASS = clazz.getName();
	   String SERVLET_WIDGET_NAME =  clazz.getSimpleName();
	   
	   return module.createServlet (SERVLET_WIDGET_CLASS, SERVLET_WIDGET_NAME, null);
   	} 
  

}
