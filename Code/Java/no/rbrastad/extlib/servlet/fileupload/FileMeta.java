package no.rbrastad.extlib.servlet.fileupload;

import java.io.InputStream;

public class FileMeta {
		 
	    private String fileName;
	    private String fileSize;
	    private String fileType;
	    private String storeLocation;
	    
	    private String fieldName;
	    private String fieldValue;
	    
 
	    private InputStream content;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileSize() {
			return fileSize;
		}

		public void setFileSize(String fileSize) {
			this.fileSize = fileSize;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public InputStream getContent() {
			return content;
		}

		public void setContent(InputStream content) {
			this.content = content;
		}

		public void setStoreLocation(String storeLocation) {
			this.storeLocation = storeLocation;
		}

		public String getStoreLocation() {
			return storeLocation;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldValue() {
			return fieldValue;
		}

		public void setFieldValue(String fieldValue) {
			this.fieldValue = fieldValue;
		}
	 
}
