<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FSA - Register</title>


<!-- <script src="https://apis.google.com/js/platform.js" async defer></script> -->
<meta name="google-signin-client_id"
	content="600561618290-lmbuo5mamod25msuth4tutqvkbn91d6v.apps.googleusercontent.com">
<!-- Google Fonts -->
<link href="https://fonts.googleapis.com/css?family=Catamaran:800"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.5.1.js"
	integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc="
	crossorigin="anonymous"></script>
<script type="application/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.2.1/js.cookie.js"></script>
<script
	src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.15.0/jquery.validate.min.js"></script>
<!-- Base URL  -->
<script src="page_dependencies/baseurl.js?v=908"></script>

<script type="text/javascript" src="js/login_validation.js?v=89"></script>
<script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/toastr.js/2.0.2/js/toastr.min.js"></script>
</head>

<body class="bgwhite">
	<%-- <%@include file="subpages/loader.jsp" %>
<%@include file="subpages/googletagmanagernoscript.jsp" %> --%>
	<nav
		class="navbar navbar-inverse bg-primary d-md-block d-sm-block d-xs-block d-lg-none d-xl-none">
		<div class="container-fluid">

			<div class="navbar-header col-md-12 text-center">

				<a class="navbar-brand text-center col-md-12 logohome" href="./">
					<!--   <img src="images/blogtrackers.png"  /> -->
				</a>

			</div>


		</div>
	</nav>


	<div id="loginPageContainer" class="container">
		<div id="loginContainer" class="container">
			<div id="loginHeader" class="container">
				<div id="loginImage" class="header">
					<img alt="Merjek Logo"
						src="${pageContext.request.contextPath}/images/mercek_8_2.jpg">
				</div>
				<div id="loginTitle" class="header">
					<h1>Welcome to FSA</h1>
				</div>
			</div>
			<form id="register_form" class="" method="post">
				<div class="form-group">
					<div class="form-login-error">
						<p id="error_message-box" style="color: red"></p>
					</div>
					<input type="text" id="name" required="required"
						class="form-control curved-form-login text-primary"
						placeholder="Full Name">
					<!-- <small id="emailHelp" class="form-text text-muted">We'll never share your email with anyone else.</small> -->
				</div>
				<div class="form-group">
					<input type="email" id="email" required="required"
						class="form-control curved-form-login text-primary"
						placeholder="Email">
				</div>

				<div class="form-group">
					<input type="password" id="password" required="required"
						class="form-control curved-form-login text-primary"
						placeholder="Password">
				</div>
				<div class="form-group">
					<input type="password" id="password2" required="required"
						class="form-control curved-form-login text-primary"
						placeholder="Re-type Password">
				</div>

				<p class="text-center float-left">
					<button type="submit" class="btn btn-primary loginformbutton mt10"
						style="background: #28a745;">Register</button>
					<!--   &nbsp;&nbsp; or Register with &nbsp;&nbsp; -->
					<!--  <button class="btn btn-rounded big-btn2"><i class="fab fa-google icon-small text-primary"></i></button> -->
					<!-- <button class="btn buttonportfolio3 mt10 pt10 pb10 pl40">
							<b class="float-left bold-text">Sign up with Google </b></button> -->
				</p>

				<p class="pb20 text-primary text-center" style="clear: both;">
					Already have an account yet? <a href="login.jsp"><b>Login
							Now</b></a></small>
				</p>
			</form>

		</div>

	</div>
	<div
		class="col-md-5 card m0 p0 bg-primary borderradiusround nobordertopleft noborderbottomleft othersection noborder">
		<div
			class="card-body borderradiusround nobordertopleft noborderbottomleft p10 pt20 pb5 robotcontainer2 text-center">
			<a class="navbar-brand text-center logohome" href="./"> </a>
		</div>
	</div>

	<!-- </div>
	</div> -->

</body>
</html>
