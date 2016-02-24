<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");
%> 
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>修改密码</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
	<script type="text/javascript" src="<%=path%>/js/public/common.js"></script>
<script type="text/javascript" src="<%=path%>/js/userInfo.js"></script>
<SCRIPT type="text/javascript">
$(function(){
$("#submit_from").click(function(){
	var p1=$("#newPass").val();
	var p2=$("#newPass1").val();
	if(p1=="")
	{
		alert("请输入新密码");
		$("#newPass").focus();
		return;
	}
	else if(p2=="")
	{
		alert("请再输入新密码");
		$("#newPass1").focus();
		return;
	}
	else if(p1.length<6)
	{
		alert("请输入不少于6位密码");
		$("#newPass").focus();
		return;}
	else if(p1!=p2)
	{
		alert("两次输入的密码不一致");
		$("#newPass").focus();
		return;
	}
	else{
		$("#userForm").submit();
	}
});
})
</SCRIPT>
</head>
<body style="background-color: transparent" class="info_mana">
<s:if test="#request.result==1">
  <script type="text/javascript">
     alert("信息修改成功!");
  </script>
</s:if>
<div class="content">
<h2>基本信息</h2>
<form action="<%=path %>/updatePass.action" method="post" id="userForm" name="userForm">
<table>
  <tr>
    <td>新密码：</td>
    <td><input name="newPass" id="newPass" value="" type="password" /></td>
  </tr>
  <tr>
    <td>再次输入新密码：</td>
    <td><input name="newPass1" id="newPass1" value="" type="password" /></td>
  </tr>
  <tr class="bu_input03">
    <td colspan="2"><input type="button" id="submit_from" value="确定" class="button_box" style="margin:10px 0 0 200px;"></td>
  </tr>
</table>
</form>
</div>
</body>

</html>