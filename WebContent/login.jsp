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
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/login.css" />

<!-- Google Fonts -->
<link href="https://fonts.googleapis.com/css?family=Catamaran:800"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.5.1.js"
	integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc="
	crossorigin="anonymous"></script>
<script type="application/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.2.1/js.cookie.js"></script>
<script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.15.0/jquery.validate.min.js"></script>
<script src="page_dependencies/baseurl.js?v=908"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/login_validation.js?v=907"></script>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.2/js/toastr.min.js"></script>

</head>

<body>
	<div id="loginPageContainer" class="container">
		<div id="loginContainer" class="container">
			<div id="loginHeader" class="container">
				<div id="loginImage" class="header">
					<img alt="Merjek Logo"
						src="${pageContext.request.contextPath}/images/mercek_8_2.jpg">
				</div>
				<div id="loginTitle" class="header">
					<h1>Focal Structure Visualization</h1>
				</div>
			</div>
			<div id="loginFormContainer" class="container">
				<!-- <form id="login_form" action="" method="post">
            <input style="margin-bottom: 10px;" id="username" type="text" placeholder="Username" autofocus required><br> 
            <input style="margin-bottom: 10px;" id="password" type="password" placeholder="Password" required><br>
            <input style="margin-top: 20px;" type="submit" id="loggin" value="Submit">
          </form> -->

				<form id="login_form" class="" method="post">
					<div class="form-group">
						<div class="form-login-error">
							<p id="error_message-box" style="color: red"></p>
						</div>
						<input type="email"
							class="form-control curved-form-login text-primary" id="username"
							autocomplete="off" required="required" required
							aria-describedby="emailHelp" placeholder="Email">
					</div>
					<br />
					<div class="form-group">
						<input type="password"
							class="form-control curved-form-login text-primary"
							required="required" autocomplete="off" required id="password"
							placeholder="Password">
						<div class="invalid-feedback">Please enter your password</div>
					</div>
					<br />
					<div class="" id="loggin2"></div>
					<div>
						<p class="float-left pt10">
							<input id="remember_me" type="checkbox"
								class="remembercheckbox blue" /><span></span>Remember Me
						</p>
						<p class="pt10 text-primary float-right">
							<small class="bold-text"><a
								href="<%=request.getContextPath()%>/forgotpassword.jsp">Forgot
									your password?</a></small>
						</p>

					</div>
					<div class="clearfloat mb50" id="loggin">
						<button type="submit"
							class="btn btn-primary loginformbutton mt10 bold-text"
							style="background: #28a745;">Login</button>
					</div>

					<p class="pb40 mt30 text-primary">
						Don't have an account with FSA? <a
							href="<%=request.getContextPath()%>/register"><b>Register
								Now</b></a></small>
					</p>
				</form>
			</div>
		</div>
	</div>

	<script type="text/javascript">
    /* $('#loggin').on("click", function(e) {
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
	} */
    </script>
</body>

</html>
