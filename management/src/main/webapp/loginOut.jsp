<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<html>

<head>
<script type="text/javascript">
function loginOut()
{
	   parent.location.href="<%=path%>/loginOut.action";
 }
</script>
</head>
<body onload="loginOut()">
</body>
</html>