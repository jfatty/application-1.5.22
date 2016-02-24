<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>

<title>登录</title>
<script type="text/javascript">

function submit_form()
{
  var userName=document.getElementById("userName");
  var userPass=document.getElementById("userPass");
  if(userName.value=="")
  {
     alert("请输入用户名");
     userName.focus();
     return
   }

  if(userPass.value=="")
  {
     alert("请输入密码");
     userPass.focus();
     return
   }

  var forms=document.getElementById("form1");
  forms.submit();
}
</script>
</head>
<body>
<h2 align="center">用户登录</h2>

<form action="<%=basePath%>login.action" method="post" id="form1" name="form1">
   <table border="1" width="400" align="center">
      <tr>
        <td>用户名</td>
        <td><input type="text" name="userName" id="userName"><font color="red">*</font> </td>
      </tr>
      <tr>
        <td>密码</td>
        <td><input type="password" name="userPass" id="userPass"><font color="red">*</font> </td>
      </tr>
       <tr>
        <td colspan="2" align="center"><input type="button" value="登录" onclick="submit_form()">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="reset" value="重置">  </td>
      </tr>
   </table>
</form>
</body>
</html>
