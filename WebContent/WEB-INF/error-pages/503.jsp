<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <title>Error!</title>
  </head>
  <body>
    <h1>we're swamped! try again later.</h1>
    <form action="${pageContext.request.contextPath}/logout" method="post">
      <button>Logout</button>
    </form>
    <h3><a href="${pageContext.request.contextPath}">try going back to the home page</a></h3>
    <img src="${pageContext.request.contextPath}/images/mercek_logo.png" class="logo" />
  </body>
</html>
