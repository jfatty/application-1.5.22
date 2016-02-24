<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>我的消息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>消息列表</h2>
<table class="table_border">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">应用名称</th>
		<th scope="col">消息格式</th>
		<th scope="col">消息状态</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.messageList==null">
	   <tr>
	      <td colspan="5">暂无消息</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.messageList" id="msg" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#msg.appName"/> </td>
	         <td><s:property value="#msg.messageFormat"/></td>
	         <td>
	         <b>
	           <s:if test="#msg.check==1">正在推送</s:if>
	           <s:elseif test="#msg.check==-1">审核没有通过</s:elseif>
	           <s:else>正在审核</s:else>
	         </b>
	         </td>
	         <td><a href="<%=path %>/queryMyMessage.action?messageId=<s:property value="#msg.id"/>" target="main">查看</a></td>
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