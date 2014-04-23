var PersonasDirectory = {	
	dataTablePersonasDirectoryId : "#datatablePersonasDirectory",
	urlPersonasDirectory : "../api/data/collections/name/personas.all.view",
	msgSavedSuccess : "Saved person.",
	msgSavedError : "An error saving.",
	msgDeleteError : "An error deleting a person.",
	msgDeleteSuccess : "Person deleted.",
	
	init : function(){
	
		$( "#newPersonBtn" ).click(function() {
			$("#newPersonContainerWell").toggle();
			$("#deletePersonBtn").hide();
			$("#newPersonBtn").hide();
			
			$("#unid").val("");	
		});
		
		$( "#cancelBtn" ).click(function() {
			$("#newPersonContainerWell").toggle();
			$("#createBtn").html("create");
			$("#newPersonBtn").show();
		});
		
		$( "#createBtn" ).click(function() {
			$("#action").val("");
			PersonasDirectory.savePerson("POST", PersonasDirectory.msgSavedSuccess, PersonasDirectory.msgSavedSuccess);
		});
		
		$( "#deletePersonBtn" ).click(function() {
			$("#action").val("delete");
			PersonasDirectory.deletePerson($("#unid").val() ,PersonasDirectory.msgDeleteSuccess, PersonasDirectory.msgDeleteError);
		});
		
		this.getPersonas();
		
	},
	deletePerson : function(unid, successMsg , errorMsg ){
		$.ajax({
		    url: "../xsp/service/person?" + unid,
		    method: "DELETE"
		}).success(function (response) {
			PersonasDirectory.notifyAlert("alert-success","Success", successMsg );
			
			$("#newPersonContainerWell").toggle();
			
			$("#createBtn").html("create");
				
			PersonasDirectory.getPersonas();
		}).fail(function () {
			PersonasDirectory.notifyAlert("alert-danger","Error", errorMsg );
		});
	},
	savePerson : function( httpMethod, successMsg , errorMsg ){
		$.ajax({
		    url: "../xsp/service/person",
		    method: httpMethod,
		    data: $("#frmPerson").serialize()
		}).success(function (response) {
			PersonasDirectory.notifyAlert("alert-success","Success", successMsg);
		    
			$("#newPersonContainerWell").toggle();
			$("#createBtn").html("create");
				
			PersonasDirectory.getPersonas();
		}).fail(function () {
			PersonasDirectory.notifyAlert("alert-danger","Error", errorMsg );		
		});
	},
	getPersonas : function(){
		var oTable = $(this.dataTablePersonasDirectoryId).dataTable( {
	        "bProcessing": false,
	        "bDestroy": true,
	        "iDisplayLength": 1000,
	    	"sAjaxDataProp": "",
	        "sAjaxSource": this.urlPersonasDirectory,
	        "bAutoWidth": true,
	        "aoColumns": [
	            { "mData": "firstname", sDefaultContent: "" },
	            { "mData": "lastname", sDefaultContent: "" },
	            { "mData": "emailaddress", sDefaultContent: "" },
	            { "mData": "companyname", sDefaultContent: "" }     
	          ]
	    } );
		    
		PersonasDirectory.dataTableOnClickEvent( PersonasDirectory.dataTablePersonasDirectoryId );  
	},
	updatePerson : function( data ){
		$("#firstname").val(data.firstname);
		$("#lastname").val(data.lastname);
		$("#emailaddress").val(data.emailaddress);
		$("#companyname").val(data.companyname);
		$("#unid").val(data.unid);
		
		$("#createBtn").html("update");
		 
		$("#deletePersonBtn").show();
		$("#newPersonContainerWell").toggle();
	},
	notifyAlert : function (alertClass, textStrong,text){
		$("#notify-alert").removeClass("alert-success");
		$("#notify-alert").removeClass("alert-danger");
		$("#notify-alert").removeClass("alert-info");
		
	    $("#action-alert-text-strong").html( textStrong );
	    $("#action-alert-text").html( text );
	   
	    $("#notify-alert").addClass(alertClass);
	    $("#notify-alert").show();
	},
	dataTableOnClickEvent : function( tableId ){
		$( tableId ).delegate('tr', 'click', function(event) { 
	    	var oTable = $( tableId ).dataTable();
	    	var aPos = oTable.fnGetPosition(this);
            if(aPos != null){
		    	var aData = oTable.fnGetData(aPos);
	            try{
	            	if( aData != null ){
		            	PersonasDirectory.updatePerson(aData);
	            	}
	            }catch(e){
	               }
            }
	    });
	}
}