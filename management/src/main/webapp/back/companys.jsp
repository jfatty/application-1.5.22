<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>公司列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css" />
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path%>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path%>/js/jquery.DOMwindow.js"></script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>公司列表信息</h2>
<table class="appTable" cellpadding="0" cellspacing="0">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">公司名称</th>
		<th scope="col">邮箱地址</th>
		<th scope="col">申请时间</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.userList==null">
		<tr>
			<td colspan="5">暂无公司信息</td>
		</tr>
	</s:if>
	<s:else>
		<s:iterator value="#request.userList" id="comp" status="status">
			<tr>
				<td><s:property value="#status.index+1" /></td>
				<td><s:property value="#comp.companyName" /></td>
				<td><s:property value="#comp.userEmail" /></td>
				<td>20<s:property value="#comp.createAt" /></td>
				<td><s:if test="#request.type=='app'">
					<a
						href="<%=path%>/appManager.action?userEmail=<s:property value="#comp.userEmail"/>"
						target="main">查看</a>
				</s:if> <s:else>
					<a
						href="<%=path%>/messageManager.action?userEmail=<s:property value="#comp.userEmail"/>"
						target="main">查看</a>
				</s:else></td>
			</tr>
		</s:iterator>
	</s:else>
</table>
<s:if test="#request.type=='app'">
	<div id="page_box" align="right" style="padding-right:20px"><page:page
		path="queryCompanyList.action?type=app" name="pageModel"
		formName="Form1" parameter="page" /></div>
</s:if> <s:else>
	<div id="page_box" align="right" style="padding-right:20px"><page:page
		path="queryCompanyList.action?type=message" name="pageModel"
		formName="Form1" parameter="page" /></div>
</s:else></div>
</body>
</html>