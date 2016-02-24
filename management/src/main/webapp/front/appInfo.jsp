<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%
  String path=request.getContextPath();
%>
<html>

  <head>
     <title>应用详情</title>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
     <link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
	 <link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
	 <script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
	 <script type="text/javascript">
	   function sel()
	   {
		   var c=document.getElementById("custom");
		  if(c.checked)
		  {
		  	document.getElementById("custom_my").style.display="";
		  }
		  else
		  {
			  document.getElementById("custom_my").style.display="none";
		  }
	   }
	 </script>
  </head>
 
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>应用详细信息</h2>
<form action="<%=path%>/updateApp.action" method="post">
<table cellspacing="10" cellpadding="0" class="userInfoTable">
	<tr>
		<td class="title">应用名称：</td>
		<td><input name="app.appName" id="appName"  value="${app.appName}" readonly="readonly" 
			type="text" class="inputText"/></td>
	</tr>
	<tr>
		<td class="title">应用类型：</td>
		<td>
		<s:select name="app.appType" theme="simple" disabled="true"  list="#{'yule':'娱乐','yinyue':'音乐','xiaoshuo':'小说','yingshi':'影视'}" cssClass="inputText"></s:select>
		</td>
	</tr>
	<tr>
		<td class="title">包名：</td>
		<td><input name="app.packageName" id="packageName" value="${app.packageName }" readonly="readonly"
			type="text" class="inputText"/></td>
	</tr>
	<tr>
		<td class="title">推送平台：</td>
		<td>
		<s:select name="app.platForm" theme="simple"  disabled="true" list="#{'ANDROID':'ANDROID','IOS':'IOS','SYMBIAN':'SYMBIAN','MTK':'MTK','BLACKBERRY':'BLACKBERRY'}" cssClass="inputText"></s:select>
			</td>
	</tr>
	<tr>
		<td class="title">消息回调地址：
		</td>
		<td><input name="app.callBackUrl" id="callBackUrl" value="${app.callBackUrl }" readonly="readonly"
			type="text" class="inputText"/></td>
	</tr>
	<tr>
		<td class="title">应用简介：</td>
		<td colspan="1"><textarea name="app.appProfile" cols="" rows="16" readonly="readonly" class="inputText">${app.appProfile }</textarea></td>
	</tr>
	<tr class="bu_check">
		<td class="title">推送类型：</td>
		<td colspan="1"><label>
		<s:checkboxlist name="app.pushMode" theme="simple" disabled="true" list="#{'one':'单条','broadcast':'广播','group':'组播'}" cssClass="inputText"></s:checkboxlist>
		
		</label></td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息生命周期：</td>
		<td colspan="1"><s:checkboxlist name="app.messageLifeCycle" disabled="true" theme="simple" list="#request.custom"></s:checkboxlist></td>
	</tr>
	<tr id="custom_my" style="display: none">
		<td class="title">请输入自定义生命周期：</td>
		<td colspan="1">
		  <input type="text" value="0" name="custom_" id="custom_" readonly="readonly"/>
		</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息格式：</td>
		<td colspan="1"><label>		
		  <s:checkboxlist  name="app.messageFormat"  disabled="true" theme="simple" list="#{'text':'文本','audio':'音乐','vedio':'视频','image':'图片','net':'网页','app':'应用','phone':'电话','local':'地理位置'}"></s:checkboxlist>
		</label></td>
	</tr>
	<s:if test="#request.app.appId!=null&&#request.app.appId!=''">
	<tr>
		<td class="title">APPID:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			${app.appId} 
		</td>
	</tr>
	<tr>
		<td class="title">KEY:&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			 ${app.appKey}
		</td>
	</tr>
	</s:if>
	<tr>
		<td class="title">审核意见：</td>
		<td colspan="1"><textarea name="app.checkView" cols="" readonly="readonly" rows="16">${app.checkView }</textarea></td>
	</tr>
	<tr>
		<td class="bu_td" colspan="2" align="center">
		   <a href="javascript:history.back()" class="button_box">返回</a>
		</td>
	</tr>
</table>
</form>
</div>
</body>
</html>