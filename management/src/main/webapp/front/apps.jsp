<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>我的应用</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
$(function(){
	    $("#menu",window.parent.document.body).contents().find("ul li a").removeClass("now");
	    $("#menu",window.parent.document.body).contents().find("#myapps").addClass("now");  
		});
</script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>我的应用</h2>
<table class="appTable" cellpadding="0" cellspacing="0">
 	<tr align="right">
	      <td colspan="5" align="right"><a href="<%=path %>/front/appRegister.jsp">申请应用ID和KEY</a></td>
	   </tr>
	<tr>
		<th scope="col" width="30">序号</th>
		<th scope="col">应用类型</th>
		<th scope="col">应用名称</th>
		<th scope="col">状态</th>
		<th scope="col" width="66">操作</th>
	</tr>
	<s:if test="#request.appList==null">
	   <tr>
	      <td colspan="5" align="center">暂无应用</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.appList" id="app" status="status">
	      <tr>
	          <td align="center"><s:property value="#status.index+1"/></td>
	          <td>
	            <s:if test="#app.appType=='yule'">娱乐</s:if>
	            <s:elseif test="#app.appType=='yinyue'">音乐</s:elseif>
	            <s:elseif test="#app.appType=='xiaoshuo'">小说</s:elseif>
	            <s:elseif test="#app.appType=='yingshi'">影视</s:elseif>
	            <s:else>工具</s:else>
	          </td>
	         <td><s:property value="#app.appName"/> </td>
	         <td><b>
	           <s:if test="#app.checkStatus==1">通过</s:if>
	           <s:elseif test="#app.checkStatus==2">通过但默认</s:elseif>
	           <s:elseif test="#app.checkStatus==0">正在审核</s:elseif>
	           <s:else>拒绝</s:else>
	           
	         </b></td>
	         <td>
	         <s:if test="#app.checkStatus==1||#app.checkStatus==2"><a href="<%=path %>/queryAppById.action?appId=<s:property value="#app.id"/>" target="main">查看</a></s:if>
	         <s:else>
	         <a href="<%=path %>/queryAppById.action?appId=<s:property value="#app.id"/>" target="main">查看</a>&nbsp;
	         <a href="<%=path %>/toUpdateApp.action?appId=<s:property value="#app.id"/>&result=0" target="main">修改</a>
	         </s:else>
	         
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<br/>
<div id="page_box" align="right" style="padding-right:20px">
<page:page path="queryMyAppList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>
</body>
</html>