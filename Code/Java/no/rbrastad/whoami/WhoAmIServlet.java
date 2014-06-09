package no.rbrastad.whoami;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.NotesException;

import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class WhoAmIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;	
	
	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		doPost(req, res);
	}
	
	public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		res.setContentType("application/json");
		res.setHeader("Cache-Control", "no-cache");
				
		JsonJavaObject returnJSON = new JsonJavaObject();
		try {
			returnJSON.put("status", true );		
			returnJSON.put("username", ExtLibUtil.getCurrentSession().getEffectiveUserName()  );
		} catch (NotesException e) {
			returnJSON.put("status", false );
			returnJSON.put("message", e.getMessage() );
		}
		
		PrintWriter out = res.getWriter (); 
		out.print( jsonToString(returnJSON) );			
		out.close ();
	}
	
	private String jsonToString(JsonJavaObject returnJSON ){
		try {
			String str = JsonGenerator.toJson(JsonJavaFactory.instanceEx, returnJSON);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
