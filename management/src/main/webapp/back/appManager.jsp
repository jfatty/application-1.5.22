<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html>

<head>
<title>应用审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>应用审核</h2>
<table class="appTable" cellpadding="0" cellspacing="0">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">应用名称</th>
		<th scope="col">状态</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.appList==null">
	   <tr>
	      <td colspan="4">暂无应用</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator  value="#request.appList" id="app"  status="status">
	      <tr>
	         <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#app.appName"/> </td>
	         <td><b>
	         <s:if test="#app.checkStatus==1">
	          	 审核通过
	         </s:if>
	         <s:elseif test="#app.checkStatus==2">
	          	审核通过但默认
	         </s:elseif>
	         <s:elseif test="#app.checkStatus==3">
	          	审核未通过
	         </s:elseif>
	          <s:elseif test="#app.checkStatus==0">
	          	未审核
	         </s:elseif>
	         <s:else>
	          	  拒绝
	         </s:else>
	         </b></td>
	         <td>
	         <s:if test="#app.checkStatus==0">
	          	<a href="<%=path %>/queryAppById.action?appId=<s:property value="#app.id"/>" target="main">详细</a>&nbsp;
	         	<a href="<%=path %>/queryApp.action?appId=<s:property value="#app.id"/>" target="main">审核</a>
	         </s:if>
	         <s:else>
	         	<a href="<%=path %>/queryAppById.action?appId=<s:property value="#app.id"/>" target="main">详细</a>&nbsp;
	         </s:else>
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<br/>
<div id="page_box" align="right" style="padding-right:20px">
<page:page path="appManager.action?userEmail=${userEmail}" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>
</body>
</html>