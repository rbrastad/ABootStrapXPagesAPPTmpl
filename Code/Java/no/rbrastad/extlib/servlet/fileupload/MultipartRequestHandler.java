package no.rbrastad.extlib.servlet.fileupload;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultipartRequestHandler {
 
    public static List<FileMeta> uploadByApacheFileUpload(HttpServletRequest request) throws IOException, ServletException{
        List<FileMeta> files = new LinkedList<FileMeta>();
        // 1. Check request has multipart content
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        FileMeta temp = null;
 
        // 2. If yes (it has multipart "files")
        if(isMultipart){
            // 2.1 instantiate Apache FileUpload classes
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
 
            // 2.2 Parse the request
            try {
 
                // 2.3 Get all uploaded FileItem
                List<FileItem> items = upload.parseRequest(request);
  
                // 2.4 Go over each FileItem
                for(FileItem item:items){
 
                    // 2.5 if FileItem is not of type "file"
                    if (item.isFormField()) {
                    	  temp = new FileMeta();
                    	  temp.setFieldName(item.getFieldName());
                    	  temp.setFieldValue(item.getString());
                    	  
                          // 2.7 Add created FileMeta object to List<FileMeta> files
                          files.add(temp);	
                    	
                    } else {
 
                        // 2.7 Create FileMeta object
                        temp = new FileMeta();
                        temp.setFileName(item.getName());
                        temp.setContent(item.getInputStream());
                        temp.setFileType(item.getContentType());
                        temp.setFileSize(item.getSize()/1024+ "Kb");   
                        
                        // 2.7 Add created FileMeta object to List<FileMeta> files
                        files.add(temp);
                    }
                } 
 
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
        }
        return files;
    }

 }