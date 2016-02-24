<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
String path=request.getContextPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>单条推送</title>
<script type="text/javascript">
 function pushClient()
 {
   var message=document.getElementById("message");
   var key=document.getElementById("key");
   var pushForm=document.getElementById("pushForm");
   if(message==null||message=="")
   {
     alert("请输入消息内容");
     message.focus();
     return;
    }
   else if(key==null||key=="")
   {
	   alert("请输入key");
	   key.focus();
	   return;
	}
   else
   {
      pushForm.submit();
	}
 }
</script>
</head>
<body>
<h2>推送单条消息</h2>
<form action="<%=path%>/pushToClient.action" id="pushForm" name="pushForm" method="post">
  <table border="1">
    <tr>
      <td>请输入key</td>
      <td><input type="text" name="key" id="key"/> </td>
    </tr>
     <tr>
      <td>请输入推送内容</td>
      <td><textarea rows="10" cols="20" name="message" id="message"></textarea> </td>
    </tr>
     <tr>
      <td colspan="2"><input type="button" value="推送到客户端" onclick="pushClient()"/> </td>
    </tr>
  </table>
</form>
</body>
</html>
