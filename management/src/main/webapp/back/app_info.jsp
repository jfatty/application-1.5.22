<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%
  String path=request.getContextPath();
%>
<html>

  <head>
     <title>应用申请</title>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
     <link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
	 <link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
	 <script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
	 <script type="text/javascript" src="<%=path%>/js/app.js"></script>
  </head>
 
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>应用详细信息</h2>
<form action="<%=path%>/checkApp.action" method="post" id="check_form">
<table>
	<tr>
		<td width="30%" class="title">应用名称：</td>
		<td width="70%"><input name="app.appName" id="appName" readonly="readonly" value="${app.appName}"
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">应用类型：</td>
		<td width="70%"><input name="app.appType" id="appType" type="text" value="${app.appType }" readonly="readonly" /></td>
	</tr>
	<tr>
		<td width="30%" class="title">包名：</td>
		<td width="70%"><input name="app.packageName" id="packageName" value="${app.packageName }" readonly="readonly" 
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">推送平台：</td>
		<td width="70%"><input name="app.platForm" id="platForm" value="${app.platForm }" readonly="readonly" 
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">消息回调地址：</td>
		<td width="70%"><input name="app.callBackUrl" id="callBackUrl" value="${app.callBackUrl }" readonly="readonly" 
			type="text"/></td>
	</tr>
	<tr>
		<td class="title">应用简介：</td>
		<td colspan="1"><textarea name="app.appProfile" cols="" rows="16" readonly="readonly" >${app.appProfile }&nbsp;</textarea></td>
	</tr>
	<tr class="bu_check">
		<td class="title">推送类型：</td>
		<td colspan="1">
		 <s:iterator value="#request.app.pushMode" id="mode" status="status">
		    <s:property value="value"/> 
		    
		 </s:iterator>
		</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息生命周期：</td>
		<td colspan="1"><s:iterator value="#request.app.messageLifeCycle" id="mode" status="status">
		    <s:property value="value"/> 
		    
		 </s:iterator></td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息格式：</td>
		<td colspan="1"><s:iterator value="#request.app.messageFormat" id="mode" status="status">
		    <s:property value="value"/> 
		    
		 </s:iterator></td>
	</tr>
	<tr>
		<td class="bu_td" colspan="2"><input type="button" value="通过" onclick="check_app('1')"
			class="bu_input" />&nbsp;&nbsp; <input type="button" value="通过但默认" onclick="check_app('2')"
			class="bu_input" />&nbsp;&nbsp;<input type="button" value="不通过" onclick="check_app('-1')"
			class="bu_input" />
			<input type="hidden" id="check_value" name="check_value" value=""/>
			</td>
	</tr>
</table>
</form>
</div>
</body>
</html>