<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%--
    Author: Fatih Şen
    Author: Andrew Pyle axpyle@ualr.edu MS Information Science 2018
    Author: Seun Johnson oljohnson@ualr.edu MS Information Science 2021
    License: MIT
--%>

<!DOCTYPE html>
<html>
  <head>
    <title>Merjek Login Page</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css"/>
    
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css?family=Catamaran:800" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.5.1.js" integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc=" crossorigin="anonymous"></script>
  </head>
  
  <body>
    <div id="loginPageContainer" class="container">
      <div id="loginContainer" class="container">
        <div id="loginHeader" class="container">
          <div id="loginImage" class="header">
            <img alt="Merjek Logo" src="${pageContext.request.contextPath}/images/mercek_8_2.jpg">
          </div>
          <div id="loginTitle" class="header">
            <h1>Focal Structure Visualization</h1>
          </div>
        </div>
        <div id="loginFormContainer" class="container">
          <form id="loginForm" action="" method="post">
            <input style="margin-bottom: 10px;" class="user_name" type="text" placeholder="Username" autofocus required><br> 
            <input style="margin-bottom: 10px;" class="pass_word" type="password" placeholder="Password" required><br>
            <input style="margin-top: 20px;" type="submit" id="loggin" value="Submit">
          </form>
        </div>
      </div>
    </div>
    
    <script type="text/javascript">
    $('#loggin').on("click", function(e) {
    	email = $('.user_name').val()
    	username = ""
    	password = $('.pass_word').val()
    	name = ""
    	pic = ""
    	register(email, username, password, pic);
	});
    function register(username, name, password, pic) {
		$("#loggin")
				.html(
						'<button type="button" class="btn btn-primary loginformbutton" style="background: #28a745;">Logging in ...</button>');

		$.ajax({
			url : 'register',
			method : 'POST',
			//dataType: 'json',
			data : {
				email : email,
				name : name,
				profile_picture : pic,
				password : "",
				register : "yes",
				signin : "yes",
			},
			error : function(response) {
				alert('error')
			},
			success : function(response) {
				toastr.success('Login successfull!', 'Success');
				window.location.href = baseurl + "dashboard.jsp";
			}
		});
	}
    </script>
  </body>
  
</html>
