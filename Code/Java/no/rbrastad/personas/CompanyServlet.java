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

public class CompanyServlet extends JsonHttpServlet {

	private static final long serialVersionUID = 1L;	
	
	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		setRequestResponse(req,res);
		
		String pCompany = req.getParameter("company");		
		JsonJavaObject returnJSON = new JsonJavaObject();
		returnJSON.put("company", pCompany);
		
		PersonasFactory personasFactory = new PersonasFactory();
		returnJSON = personasFactory.getPersonsByCompanyName(pCompany);
		
		doResponseWriteJson(returnJSON);	
	}

	
	
	public void doPost (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException { 
		setRequestResponse(req,res);
	
	//	doResponseWriteJson(returnJSON);
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
