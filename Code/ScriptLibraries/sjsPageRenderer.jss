var PageRenderer = {		
	renderContentFacet: function() {
		try{
			var indexPage = "/page.xsp/";
			var exCon = facesContext.getExternalContext();
			var request = exCon.getRequest();
			var uri = request.getRequestURI();	
			var path = request.getContextPath();
			
			uri = uri.replace(path ,"");
			uri = uri.replace(indexPage ,"");
			
			var urlCheck = uri.substring(0,2);
			if(uri.substring(0,1) == "/")
				uri = uri.substring(1);
						
			if( uri == "" ){
				exCon.redirect(path + "/page.xsp/index");
			}
				
			return uri;
		}catch(exception){
			return "error";
		}
	},
	renderTemplateFacet: function() {
		try{
			var indexPage = "/template.xsp/";
			var exCon = facesContext.getExternalContext();
			var request = exCon.getRequest();
			var uri = request.getRequestURI();	
			var path = request.getContextPath();
			
			uri = uri.replace(path ,"");
			uri = uri.replace(indexPage ,"");
			
			return uri;
		}catch(exception){
			return "error";
		}
	},
	getUniqueID : function (){
		return java.util.UUID.randomUUID().toString();
	}
};