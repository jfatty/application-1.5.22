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
<table>
	<tr>
		<td width="30%" class="title">应用名称：</td>
		<td width="70%"><input name="app.appName" id="appName"  value="${app.appName}"
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">应用类型：</td>
		<td width="70%">
		<s:select name="app.appType" theme="simple"  list="#{'yule':'娱乐','yinyue':'音乐','xiaoshuo':'小说','yingshi':'影视'}"></s:select>
	</tr>
	<tr>
		<td width="30%" class="title">包名：</td>
		<td width="70%"><input name="app.packageName" id="packageName" value="${app.packageName }"
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">推送平台：</td>
		<td width="70%">
		<s:select name="app.platForm" theme="simple"  list="#{'ANDROID':'ANDROID','IOS':'IOS','SYMBIAN':'SYMBIAN','MTK':'MTK','BLACKBERRY':'BLACKBERRY'}"></s:select>
			</td>
	</tr>
	<tr>
		<td width="30%" class="title">消息回调地址：
		</td>
		<td width="70%"><input name="app.callBackUrl" id="callBackUrl" value="${app.callBackUrl }"
			type="text"/></td>
	</tr>
	<tr>
		<td class="title">应用简介：</td>
		<td colspan="1"><textarea name="app.appProfile" cols="" rows="16">${app.appProfile }&nbsp;</textarea></td>
	</tr>
	<tr class="bu_check">
		<td class="title">推送类型：</td>
		<td colspan="1"><label>
		<s:checkboxlist name="app.pushMode" theme="simple" list="#{'one':'单条','broadcast':'广播','group':'组播'}"></s:checkboxlist>
		
		</label></td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息生命周期：</td>
		<td colspan="1"><label><input name="messageLifeCycle"
			type="checkbox" checked="checked" value="default"/>24小时</label><label><input
			name="messageLifeCycle" type="checkbox" id="custom" value="custom" onclick="sel()"/>自定义</label><label><input
			name="messageLifeCycle" type="checkbox" value="never"/>永久</label></td>
	</tr>
	<tr id="custom_my" style="display: none">
		<td class="title">请输入自定义生命周期：</td>
		<td colspan="1">
		  <input type="text" value="0" name="custom_" id="custom_">
		</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息格式：</td>
		<td colspan="1"><label>		
		  <s:checkboxlist name="app.messageFormat" theme="simple" list="#{'txt':'文本','audio':'音乐','vedio':'视频','image':'图片','net':'网页','app':'应用','phone':'电话','local':'地理位置'}"></s:checkboxlist>
		</label></td>
	</tr>
	<tr>
		<td class="bu_td" colspan="2"><input type="submit" value="提交申请"
			class="bu_input" /> <input type="button" value="重  置"
			class="bu_input" /></td>
	</tr>
</table>
</form>
</div>
</body>
</html>