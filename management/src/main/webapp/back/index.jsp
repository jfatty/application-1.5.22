<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<html xmlns="http://www.w3.org/1999/xhtml">

  <head>
<title>登录成功</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css"/>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript"> 
$(document).ready(function(){
	$('#divAnchor').openDOMWindow({ 
		eventType:'click',
		windowSourceID:'#inlineContent', 
		loader:1, 
		loaderImagePath:'animationProcessing.gif', 
		loaderHeight:16, 
		loaderWidth:17 
	}); 
	
	$('.example5closeDOMWindow').closeDOMWindow({eventType:'click'}); 
	$("#kill_box").click(function(){
		$("#newPass").val("");
		$("#newPass1").val("");
		$('.example5closeDOMWindow').click();});
	$(".kill_box").click(function(){
		var p1=$("#newPass").val();
		var p2=$("#newPass1").val();
		if(p1=="")
		{
			alert("请输入新密码");
			$("#newPass").focus();
			return;
		}
		else if(p1.length<6)
		{
			alert("请输入不少于6位密码");
			$("#newPass").focus();
			return;
		}
		else if(p1.length>15)
		{
			alert("请输入不大于15位密码");
			$("#newPass").focus();
			return;
		}
		else if(p2=="")
		{
			alert("请再输入新密码");
			$("#newPass1").focus();
			return;
		}
		else if(p1!=p2)
		{
			alert("两次输入的密码不一致");
			$("#newPass").focus();
			return;
		}
		else{
			
		$('.example5closeDOMWindow').click();
		$("#forms").submit();
		}
	});
	 
});
function showDiv(){
	$("#divAnchor").click();
}	 
</script>
</head>
<body class="bg_body">
<iframe allowtransparency="true" framespacing="0" frameborder="0" scrolling="no" width="100%" height="90" src="<%=path %>/back/header.jsp"></iframe>
<div class="middle" style="width:902px;margin:0 auto;padding-top:10px;">
  <iframe  allowtransparency="true" framespacing="0" frameborder="0" scrolling="no" width="132" height="510" src="<%=path %>/back/menu.jsp" id="menu" name="menu" target="main"></iframe>
  <iframe allowtransparency="true" framespacing="0" frameborder="0" scrolling="auto" width="760" height="510" src="<%=path %>/managerInfo.action" id="mail" name="main" target="_self"></iframe>
</div>
<iframe allowtransparency="true" border="0" vspace="0" hspace="0" marginwidth="0" marginheight="0" framespacing="0" frameborder="0" scrolling="no" width="100%" height="90" src="<%=path %>/back/footer.html"></iframe>
<a  href="#inlineContent" id="divAnchor"  style="display:none"></a>
<div id="inlineContent" style=" display:none;"><!--这是一个弹出消息-->
<div  class="pop_box">
        <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
        <h2>修改密码</h2>
<form action="<%=path %>/updatePass.action" id="forms">
<table cellspacing="10" cellpadding="0" class="userInfoTable">
  <tr>
    <th>新密码：</th>
    <td><input name="newPass" id="newPass" value="" type="password" maxlength="14" class="inputText"/></td>
  </tr>
  <tr>
    <th>再次输入新密码：</th>
    <td><input name="newPass1" id="newPass1" value="" maxlength="14" type="password" class="inputText" /></td>
  </tr>
  <tr class="bu_input03" style="text-align:center;">
    <td colspan="2" style="text-align:center;"><input class="kill_box" type="button" value="确 定" style="display:inline;"/>&nbsp;<input id="kill_box" style="display:inline;" type="button" value="取消"/></td>
  </tr>
</table>
</form>
</div> 
</div>
</body>
</html>