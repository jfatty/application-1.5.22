<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>我的应用</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>我的应用</h2>
<table class="table_border">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">应用类型</th>
		<th scope="col">应用名称</th>
		<th scope="col">状态</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.appList==null">
	   <tr>
	      <td colspan="5">暂无应用</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.appList" id="app" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	          <td>工具类</td>
	         <td><s:property value="#app.appName"/> </td>
	         <td><b>
	           <s:if test="#app.checkStatus==1">通过</s:if>
	           <s:elseif test="#app.checkStatus==2">通过但默认</s:elseif>
	           <s:elseif test="#app.checkStatus==0">正在审核</s:elseif>
	           <s:else>拒绝</s:else>
	           
	         </b></td>
	         <td>
	         <a href="<%=path %>/queryMyApp.action?appId=<s:property value="#app.id"/>" target="main">修改</a>&nbsp;
	         <a href="<%=path %>/queryMyApp.action?appId=<s:property value="#app.id"/>" target="main">查看</a>
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<div class="page_box"><a href="#">上一页</a><a href="#"
	class="bu_now">1</a><a href="#">2</a><a href="#">3</a><a href="#">4</a><a
	href="#">下一页</a></div>
</div>
</body>
</html>