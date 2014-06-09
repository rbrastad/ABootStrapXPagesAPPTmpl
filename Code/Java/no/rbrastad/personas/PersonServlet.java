package no.rbrastad.personas;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.rbrastad.extlib.servlet.JsonHttpServlet;

import lotus.domino.Document;
import lotus.domino.NotesException;

import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PersonServlet extends JsonHttpServlet {

	private static final long serialVersionUID = 1L;	
	
	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		setRequestResponse(req,res);
		
		JsonJavaObject returnJSON = getResponseJson("GET",false,"Nothing to get");		

		doResponseWriteJson(returnJSON);	
	}

	public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		setRequestResponse(req,res);
		
//		Get parameter values from the url.
		String pFirstName = req.getParameter("firstname");
		String pLastName = req.getParameter("lastname");
		String pEmail = req.getParameter("emailaddress");
		String pCompanyName = req.getParameter("companyname");
		String pUnid = req.getParameter("unid");
		String pAction = req.getParameter("action");
		
		JsonJavaObject returnJSON = new JsonJavaObject();
		
		try {
			Document document = null;
			if(pUnid != null && !"".equals(pUnid))
				document = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(pUnid);
			else
				document = ExtLibUtil.getCurrentDatabase().createDocument();
			
			document.replaceItemValue("Form", "person");
			document.replaceItemValue("firstname", pFirstName);
			document.replaceItemValue("lastname", pLastName);
			document.replaceItemValue("emailaddress", pEmail);
			document.replaceItemValue("companyname", pCompanyName);
			
			document.save(true);
			document.recycle();
			
			returnJSON = getResponseJson("created",true,"Person created");		
		} catch (NotesException e1) {
			returnJSON = getResponseJson("created",false,e1.getMessage());	
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
		}		

		doResponseWriteJson(returnJSON);
	}
	

	public void doDelete (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 	
//		Get the document universal id form querystring. 
		String pUnid = req.getQueryString();
		try {
			Document document = null;
			if(pUnid != null && !"".equals(pUnid))
				document = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(pUnid);
		
			document.remove(true);
			document.recycle();
	
			res.setStatus(HttpServletResponse.SC_ACCEPTED);
		} catch (NotesException e1) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}	
	}
}
