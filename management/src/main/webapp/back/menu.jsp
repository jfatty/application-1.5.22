<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css"/>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript">
$(function(){
	$("li a:first").addClass("now");
});
$(function(){
	$("li a").click(function (){
		$("li a").removeClass("now");
		$(this).addClass("now");
		});
});
</script>
</head>
<body style="background-color: transparent">
	        <div class="sidebar">
            <h2>菜单</h2>
            <ul id="ul">
            <li><a href="<%=path %>/queryUser.action" target="main">用户信息管理</a></li>
            <li><a href="<%=path %>/queryCheckAppList.action" target="main" id="a_c">应用审核</a></li>
            <li><a href="<%=path %>/queryCompanyList.action?type=app" target="main">应用管理</a></li>
            <li><a href="<%=path %>/queryCheckMessageList.action" id="m_c" target="main">消息审核</a></li>
            <li><a href="<%=path %>/queryCompanyList.action?type=message" target="main">消息管理</a></li>
            <li><a href="<%=path %>/queryWordsList.action" target="main">敏感词</a></li>
            <li><a href="<%=path %>/initLicence.action" target="main">Licence管理</a></li>
            <!-- 
            <li><a href="<%=path %>/queryUserList.action" target="main">用户管理</a></li>
             -->
           <li><a href="##" onclick="javascript:if(confirm('确定退出?'))location.href='<%=path %>/loginOut.jsp' ">退出注销</a></li>
            </ul>
        </div>
</body>
</html>
