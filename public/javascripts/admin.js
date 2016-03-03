function deletebyId(id){
	var confirm_delete = confirm(translation.adminDeleteConfirmation);
    if (!confirm_delete) {
        console.log("delete canceled");
        return;
    }
    console.log("delete " + config.deleteUserUrl + id);
	$.ajax(config.deleteUserUrl + id, {
		type: "post", 
		success: function(data){
			window.location.reload();
		}
	});
};

function createUser(){
	var login = $('#login').val();
	var password = $('#password').val();
	var email = $('#email').val();
	var role= $('#role option:selected').val();

	var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;

    if (reg.test(email) == false){
        alert('Adresse email invalide');
        return 
    }

    var userJson= "{ \"login\":\""+login +"\","+
	        "\"password\":\""+password+"\","+
		"\"email\":\""+email+"\" ,"+
		"\"role\":\""+role +"\"}";
    
	$.ajax(config.createUserUrl, {
		data: userJson,
		type: "post", contentType: "application/json",
		datatype: "json",
		success: function(data){
			window.location.reload();	
		},
		error: function(){
			// TODO: ?§/.?
			$("#js-search-ajax-error-block").show();
		}
	});
};
