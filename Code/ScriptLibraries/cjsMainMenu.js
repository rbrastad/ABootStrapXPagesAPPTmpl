var MainMenu = function () {
	
	return { init: init };
	
	function init () {
		var mainnav = $('#main-nav'),
			openActive = mainnav.is ('.open-active'),
			navActive = mainnav.find ('> .active');

		mainnav.find ('> .dropdown > a').bind ('click', navClick);
		
		if (openActive && navActive.is ('.dropdown')) {			
			navActive.addClass ('opened').find ('.sub-nav').show ();
		}
		
		closeAll();
	}
	
	function navClick (e) {
		e.preventDefault ();
		
		var li = $(this).parents ('li');		
		
		if (li.is ('.opened')) { 
			closeAll ();			
		} else { 
			closeAll ();
			li.addClass ('opened').find ('.sub-nav').slideDown ();			
		}
	}
	
	function closeAll () {	
		$('.sub-nav').slideUp ().parents ('li').removeClass ('opened');
	}
}();