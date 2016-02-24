<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
String userEmail=(String)request.getSession().getAttribute("userEmail");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="../css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="../css/item_f.css"/>
<title></title>
</head>

<body style="background-color: transparent">
	<div class="header clearfix">
        <div class="logo">
        </div>
        <div class="header_right">
         <p>当前用户：<%=userEmail%><a  href="javascript:parent.window.showDiv();" class="defaultDOMWindow" >修改密码</a>
        <!--  
           <p>当前用户：<%=userEmail%><a  href="javascript:parent.window.showDiv();" class="defaultDOMWindow" >修改密码</a>-->
<!-- [<a href="load.html" target="_blank">登 陆</a>|<a href="reg.html" target="_blank">注 册</a>]-->
           </p>
        </div>
    </div>
</body>
</html>
