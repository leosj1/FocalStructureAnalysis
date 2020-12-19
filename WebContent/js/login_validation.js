/* ------------------------------------------------------------------------------
 *
 *  # Login form with validation
 *
 *  Specific JS code additions for login_validation.html page
 *
 *  Version: 1.0
 *  Latest update: Aug 5, 2018
 *
 * ---------------------------------------------------------------------------- */

//define baseurl
var baseurl = app_url;

$(function() {

//var favoriteblogs = Cookies.get('allfavoritesblogs');
//console.log(favoriteblogs);
	
	$(".form-validate").validate({
		ignore: 'input[type=hidden], .select2-search__field', // ignore hidden fields
		errorClass: 'validation-error-label',
		successClass: 'validation-valid-label',
		highlight: function(element, errorClass) {
			$(element).removeClass(errorClass);
		},
		unhighlight: function(element, errorClass) {
			$(element).removeClass(errorClass);
		},

		// Different components require proper error label placement
		errorPlacement: function(error, element) {

			// Styled checkboxes, radios, bootstrap switch
			if (element.parents('div').hasClass("checker") || element.parents('div').hasClass("choice") || element.parent().hasClass('bootstrap-switch-container') ) {
				if(element.parents('label').hasClass('checkbox-inline') || element.parents('label').hasClass('radio-inline')) {
					error.appendTo( element.parent().parent().parent().parent() );
				}
				else {
					error.appendTo( element.parent().parent().parent().parent().parent() );
				}
			}

			// Unstyled checkboxes, radios
			else if (element.parents('div').hasClass('checkbox') || element.parents('div').hasClass('radio')) {
				error.appendTo( element.parent().parent().parent() );
			}

			// Input with icons and Select2
			else if (element.parents('div').hasClass('has-feedback') || element.hasClass('select2-hidden-accessible')) {
				error.appendTo( element.parent() );
			}

			// Inline checkboxes, radios
			else if (element.parents('label').hasClass('checkbox-inline') || element.parents('label').hasClass('radio-inline')) {
				error.appendTo( element.parent().parent() );
			}

			// Input group, styled file input
			else if (element.parent().hasClass('uploader') || element.parents().hasClass('input-group')) {
				error.appendTo( element.parent().parent() );
			}

			else {
				error.insertAfter(element);
			}
		},
		validClass: "validation-valid-label",
		success: function(label) {
			label.addClass("validation-valid-label").text("")
		},
		rules: {
			password: {
				minlength: 5
			}
		},
		messages: {
			username: "Enter your Email",
		}

	});


	$('#login_form').submit( function(e) {
		e.preventDefault();
		var email = $("input#username").val();
		var password= $("input#password").val();
		if(email=="" || password=="" ){
			return false;
		}
		$("#loggin").html('<button type="submit" class="btn btn-primary loginformbutton" disabled style="background: #28a745;">Logging in ...</button>');
		var btntext = '<button type="submit" class="btn btn-primary loginformbutton mt10" style="background: #28a745;">Login</button>';
		var password = $("#password").val(); //CODE REVIEW: why do we have password variable here? - wale
			$.ajax({
				url: baseurl+'login',    //Please note the baseurl here is the deployment url not to be confused with the elastic index
				method: 'POST',
				data: {
					email: $("input#username").val(),
					password: $("input#password").val(),
					remember: $("input#remember_me").val(),
					login: "yes",
				},
				error: function(response)
				{	
					$("#error_message-box").html('Invalid email/password');
					$("#loggin").html(btntext);
		
				},
				success: function(response)
				{       
					
					var login_status = response;//.responseText;
					// console.log(login_status);
					console.log(response);
					alert(response)
					if(login_status === "invalid"){
						$("#error_message-box").html('Invalid email/password');
						$("#loggin").html(btntext);
					}else if(login_status == "success"){
						toastr.success('Login successfull!','Success');
						// set a cookie for logged in user
						
						Cookies.set('loggedinstatus', true , {path : '/'});
						window.location.href = baseurl+"index.jsp";
					}else if(login_status == "confirmed"){
						window.location.href = baseurl+"index.jsp";
					}
					return false;
				}
			});
		
		return false;
		
	});
	
	
	$('#register_form').submit( function(e) {
		e.preventDefault();
		var password1 =  $("input#password").val();
		 var password2 = $("input#password2").val();
		 var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");
		 var email = $("input#email").val();
			var name= $("input#name").val();
			if(email=="" || password=="" || name=="" ){
				 //$("#error_message-box").html('All fields are required');
				 toastr.error('All fields are required','Error');
				return false;
			}
			else if(!strongRegex.test(password1))
				{
				 $("#error_message-box").html('Password must contain minimum of 8 characters and contain at least 1 uppercase, 1 lowercase and a special character (!@#$%&)');
				// toastr.error('Password must contain minimum of 8 characters and contain at least 1 uppercase, 1 lowercase and a special character (!@#$%&)','Error');
				 return false;
				}
			else if(password1!=password2){
			//toastr.error('Password does not match','Error');	
			 $("#error_message-box").html('Password does not match');
			 return false;
		 }
			else
				{
			$.ajax({
				url: baseurl+'register',
				method: 'POST',
				//dataType: 'json',
				data: {
					email: $("input#email").val(),
					password: $("input#password").val(),
					name: $("input#name").val(),
					register: "yes",
				},
				error: function(response)
				{						
					console.log(response);
					$("#error_message-box").html('Email is in use');
					//alert("An error occoured!");
				},
				success: function(response)
				{       
					console.log(response);
					//alert("An error occoured!");
					var login_status = response;//.responseText;
					// console.log(login_status);
					if(login_status === "exists"){
						$("#error_message-box").html('Email is in use');
						toastr.error('Email is in use','Error');
					}else if(login_status == "success"){
						toastr.success('Registration successfull!','Success');
//						
						$("#error_message-box").html("");
						$("input#email").val("");
						$("input#name").val("");
						$("input#password").val("");
						$("input#password2").val("");
						window.location.href = baseurl+"login.jsp";
					}else{
						//$("#error_message-box").html('Registration Error Please try again later');
						toastr.error('Registration Error Please try again later','Error');
					}
					return false;
				}
			});
		
		return false;
				}
		
	});
	
	
});



function verify(element){
	var id = element.id;
	var content = element.value;
	var field = "";
	if(id==="username_validate"){
		field = "username";
	}else if(id==="email_validate"){
		field = "email";
	}

	if(content==""){
		return false;
	}
	$.ajax({
		url: baseurl+'validate',
		method: 'POST',
		//dataType: 'json',
		data: {
			field:field,
			value:content,
			validate: "yes",
		},
		error: function(response)
		{						
			// console.log(response);
			//$("#error_message-box").html('Invalid username/password');
			//alert("An error occoured!");
		},
		success: function(response)
		{       
			var status = response;//.responseText;

			if(status === "yes"){
				//console.log(status);
				if(field==="username"){
					$("#user_exist").html('<span class="help-block text-danger"><i class="icon-cancel-circle2 position-left"></i> This username is already taken</span>');
					$("#user_exist").removeClass("validation-valid-label");
				}

				if(field==="email"){
					$("#email_exist").html('<span class="help-block text-danger" ><i class="icon-cancel-circle2 position-left"></i> This email is already taken</span>');
					$("#email_exist").removeClass("validation-valid-label");
				}
			}else if(status === "no"){
				//  console.log(status);
				if(field==="username"){
					$("#user_exist").html('');
					$("#user_exist").addClass("validation-valid-label").text("Available");
				}

				if(field==="email"){
					$("#email_exist").html('');
					$("#email_exist").addClass("validation-valid-label").text("Available");
				}

			}
		}
	});

}


function verifypassword(element){
	var id = element.id;
	var content = element.value;
	var field = "password";

	//console.log("submitted");
	var password1 = $("#password1").val();
	var password2 = $("#password2").val();
	var old = $("#old_passord").val();

	if(password1!=password2){
		$("#invalid").html('<span class="help-block text-danger" ><i class="icon-cancel-circle2 position-left"></i>New password does not match</span>');
		$("#invalid").removeClass("validation-valid-label");
		$("#submit").prop("disabled",true);
		return false;
	}else{
		$("#invalid").addClass("validation-valid-label").text("");
		$("#submit").prop("disabled",false);
	}


}



$("body").removeClass("loaded");
$(document).ready(function(e)
{
$('a').on("click",function(e){
$("body").removeClass('loaded');
	  });
	 
$("body").addClass("loaded");
});