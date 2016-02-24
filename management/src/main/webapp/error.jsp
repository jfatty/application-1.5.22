<html>
<head>
<meta http-equiv="Content-Type" content="text/plain; charset=UTF-8">
<%
  String path=request.getContextPath();
%>
<title>页面错误</title>
</head>
<body>
<center>
  <a href="login.jsp">
    <img alt="对不起，服务器错误!" src="<%=path %>/images/error.png">
  </a>
</center>
</body>
</html>
