package no.rbrastad.extlib.servlet.fileupload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.rbrastad.extlib.servlet.service.JsonHttpServlet;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.EmbeddedObject;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;

import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class AttachmentServlet extends JsonHttpServlet {

	 private static final long serialVersionUID = 1L;
	 
	 private static List<FileMeta> files = new LinkedList<FileMeta>();
	    
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		 	setRequestResponse(request,response);
			
	    	String pUnid = request.getParameter("unid");
	        String fileName = null;
	    	
	    	files.addAll(MultipartRequestHandler.uploadByApacheFileUpload(request));
	 
	        for(FileMeta fileMeta : files){
	        	try {
	        		Database currDb = ExtLibUtil.getCurrentDatabase();
					Document document = currDb.getDocumentByUNID( pUnid );
					
					File attachmentFile = new File( fileMeta.getFileName() );
					FileOutputStream fileOutputStream = new FileOutputStream( attachmentFile );
	 
					int read = 0;
					byte[] bytes = new byte[1024];
	 
					while ((read = fileMeta.getContent().read(bytes)) != -1) {
						fileOutputStream.write(bytes, 0, read);
					}
					
			        if(document.hasItem("image"))
			        	document.removeItem("image");
			         
			    	document.replaceItemValue("imageType", fileMeta.getFileType());
			    	document.replaceItemValue("imageFileName", fileMeta.getFileName());
			        
			        RichTextItem me = document.createRichTextItem("image"); 
			        me.embedObject(EmbeddedObject.EMBED_ATTACHMENT, "", attachmentFile.getAbsolutePath(), null);
			        
			        document.save(true,true);
			        
					document.recycle();
			
					attachmentFile.delete();
					
					JsonJavaObject returnJSON = new JsonJavaObject();
					returnJSON.put("action", "person.image" );		
					returnJSON.put("status", true );		
					returnJSON.put("unid", pUnid );
					returnJSON.put("fileName", fileName );
					
					doResponseWriteJson(returnJSON);
					
					response.setStatus(HttpServletResponse.SC_ACCEPTED);
		    	} catch (NotesException e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    	}
	        }
	     }

	    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
	    	String pUnid = request.getParameter("unid");
	    	String pFile = request.getParameter("file");
		        
        	try {
        		Database currDb = ExtLibUtil.getCurrentDatabase();
				Document document = currDb.getDocumentByUNID( pUnid );			        
		         
				BufferedInputStream bis = null;
				if(document.hasItem("image")){
		        	EmbeddedObject embeddedObject  = document.getAttachment(pFile);
		        	if (embeddedObject != null) {
		        		bis = new BufferedInputStream(embeddedObject.getInputStream());
		        		
		        		response.setContentType( document.getItemValueString("imageType") );  
		        		response.setHeader("Cache-Control", "no-cache");
		        	    response.setDateHeader("Expires", -1);
		        	    response.setHeader("Content-Disposition", "attachment; filename=" + embeddedObject.getName());
		        		
		        		embeddedObject.recycle();
		        		document.recycle();
						
						ServletOutputStream op = response.getOutputStream();
						int bytesA = bis.available();
				        byte [] attachment = new byte[bytesA];
			            while (true) {
				            int bytesRead = bis.read(attachment, 0, attachment.length);
				            if (bytesRead < 0)
				            	break;
			                    op.write(attachment, 0, bytesRead);
			            }
			            bis.close();
			            op.flush();
			            op.close();
		        	}else{
						document.recycle();
		        		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		        	}  		
		        }
				 
			} catch (NotesException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
	    }

	    protected void doDelete(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
	    	String pUnid = request.getParameter("unid");
	        
        	try {
        		Database currDb = ExtLibUtil.getCurrentDatabase();
				Document document = currDb.getDocumentByUNID( pUnid );
				        
		        if(document.hasItem("image"))
		        	document.removeItem("image");
		        
		        document.save(true,true);
		        
				document.recycle();
				
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			} catch (NotesException e) {
				e.printStackTrace();
				 response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					
			}
        
	    }
	
	    
	    public static byte[] getBytesFromInputStream(InputStream inStream)throws IOException {
		     // Get the size of the file
		    long streamLength = inStream.available();
	
		     if (streamLength > Integer.MAX_VALUE) {
		    // File is too large
		    }
	
		     // Create the byte array to hold the data
		    byte[] bytes = new byte[(int) streamLength];
	
		     // Read in the bytes
		    int offset = 0;
		    int numRead = 0;
		    while (offset < bytes.length
		     && (numRead = inStream.read(bytes,
		     offset, bytes.length - offset)) >= 0) {
		     offset += numRead;
		    }
	
		     // Ensure all the bytes have been read in
		    if (offset < bytes.length) {
		    throw new IOException("Could not completely read file ");
		    }
	
		     // Close the input stream and return bytes
		    inStream.close();
		    return bytes;
	    }
}
