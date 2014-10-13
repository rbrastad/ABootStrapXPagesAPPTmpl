package no.rbrastad.personas;

import java.util.ArrayList;
import java.util.List;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PersonasFactory {
	
	public JsonJavaObject getPersonsByCompanyName(String companyName){
		JsonJavaObject responseJson = new JsonJavaObject();
		try {
			View personasView = ExtLibUtil.getCurrentDatabase().getView("personas.all.view");
			ViewEntryCollection viewEntries = personasView.getAllEntriesByKey(companyName);
			ViewEntry viewEntry = viewEntries.getFirstEntry();
			
			List<Object> list = new ArrayList<Object>();
			if(viewEntry != null){
				do{
					JsonJavaObject entryJson = new JsonJavaObject();
				
					Document document = viewEntry.getDocument();
				
					entryJson.put("name", document.getItemValueString("firstname") );
					
					document.recycle();
					
					viewEntry = viewEntries.getNextEntry(viewEntry);
					
					list.add(entryJson);
				}while(viewEntry != null);
					
			}
			
			responseJson.putList("persons",list);
			
			personasView.recycle();
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		
	return responseJson;
	}
	

}
