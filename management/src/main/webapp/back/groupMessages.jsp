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
<title>消息审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>

<script type="text/javascript">

</script>
</head>
<body>
<div class="content">
<h2>消息审核列表</h2>
<table class="table_border">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">消息内容</th>
		<th scope="col">
		 <input type="checkbox" name="all" id="all"/>全选
		</th>
	</tr>
	<s:if test="#request.messageList==null">
	   <tr>
	      <td colspan="3">暂无消息</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.messageList" id="msg" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#msg.content"/> </td>
	         <td>
	            <input type="checkbox" value="${msg.id}" name="messageId"/>
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<div id="page_box" align="right">
<page:page path="queryCheckMessageList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>
</body>
</html>