var PersonasDirectory = {	
	dataTablePersonasDirectoryId : "#datatablePersonasDirectory",
	urlPersonasDirectory : "../api/data/collections/name/personas.all.view",
	urlPersonasPersonSave: "../xsp/service/person",
	urlPersonasPersonImage : "../xsp/service/attachment?unid=",
	msgSavedSuccess : "Saved person.",
	msgSavedError : "An error saving.",
	msgDeleteError : "An error deleting a person.",
	msgDeleteSuccess : "Person deleted.",
	validator : null,
	imageUploaderAdded : false,
	
	init : function(){
		$("#personImageShow").hide();
	
		$( "#newPersonBtn" ).click(function() {
			$("#newPersonContainerWell").toggle();
			$("#deletePersonBtn").hide();
			$("#newPersonBtn").hide();
			$("#personImageContainer").hide();
			$("#unid").val("");	
		});
		
		$( "#cancelBtn" ).click(function() {
			$("#newPersonContainerWell").toggle();
			$("#createBtn").html("create");
			$("#newPersonBtn").show();
		});
		
		$( "#createBtn" ).click(function() {
			$("#action").val("");
		
			isValidated = PersonasDirectory.formValidate();
			if(isValidated) 
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
		    url: PersonasDirectory.urlPersonasPersonSave + "?" + unid,
		    method: "DELETE"
		}).success(function (response) {
			PersonasDirectory.notifyAlert("alert-success","Success", successMsg );
			
			$("#newPersonContainerWell").toggle();	
			$("#createBtn").html("create");
			
			$("#newPersonBtn").show();
			$("#deletePersonBtn").hide();
				
			PersonasDirectory.getPersonas();
		}).fail(function () {
			PersonasDirectory.notifyAlert("alert-danger","Error", errorMsg );
		});
	},
	savePerson : function( httpMethod, successMsg , errorMsg ){
		$.ajax({
		    url: PersonasDirectory.urlPersonasPersonSave,
		    method: httpMethod,
		    data: $("#frmPerson").serialize()
		}).success(function (response) {
			
			console.log(response);
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
	
		$("#newPersonBtn").hide();
		$("#deletePersonBtn").show();
		$("#newPersonContainerWell").show();
	
		if(data.imageFileName != null){
			$("#personImageShow").attr("src", PersonasDirectory.urlPersonasPersonImage + data.unid +"&file=" +  data.imageFileName );
			$("#personImageShow").show();
			$("#personAvatar").hide();
		}
		
//		A quick and dirty example to set a url with the unid of the document
		if( !PersonasDirectory.imageUploaderAdded ){
			$("#personImage").uploadFile({
				url: PersonasDirectory.urlPersonasPersonImage + data.unid,
				onSuccess : function(response){
					console.log(response);
					PersonasDirectory.getPersonas();
				}
			});
			PersonasDirectory.imageUploaderAdded = true;
		}else{
			$("#personImage").update({url: PersonasDirectory.urlPersonasPersonImage + data.unid});
		}
			
			
		$(".ajax-upload-dragdrop").removeAttr("style");
		
		$("#personImageContainer").show();	
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
	},
	formValidate : function() {
		validator = PersonasDirectory.validator;
		if(validator == null){
			validator = $('#frmPerson').parsley();
//			A validator field success helper method that hides the text box completely
			validator.subscribe('parsley:field:success', function (data) {
			   $("#parsley-id-" + data.__id__).removeClass("parsley-errors-list");
			   return;
			  });
//			A validator field success helper method that adds the parsley-errors-list class on field error.			
			validator.subscribe('parsley:field:error', function (data) {
				$("#parsley-id-" + data.__id__).addClass("parsley-errors-list");
			    return;
			  });
		}
		
		return validator.validate();
	}
}