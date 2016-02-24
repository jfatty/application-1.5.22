<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	 <script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
	 <script type="text/javascript" src="<%=path%>/js/app.js"></script>
  </head>
 
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>申请AppID、Key</h2>
<form action="<%=path%>/addApp.action" method="post">
<table>
	<tr>
		<td width="30%" class="title">应用名称：</td>
		<td width="70%"><input name="app.appName" id="appName"
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">应用类型：</td>
		<td width="70%">	
		<select name="app.appType" id="appType" style="width: 40%;">
		   <option value="yule">娱乐</option>
		   <option value="yinyue">音乐</option>
		   <option value="xiaoshuo">小说</option>
		   <option value="yingshi">影视</option>
		</select>	</td>
	</tr>
	<tr>
		<td width="30%" class="title">包名：</td>
		<td width="70%"><input name="app.packageName" id="packageName"
			type="text"/></td>
	</tr>
	<tr>
		<td width="30%" class="title">推送平台：</td>
		<td width="70%"><select name="app.platForm" id="platForm"  style="width: 40%;">
		    <option value="ANDROID">ANDROID</option>
		    <option value="IOS">IOS</option>
		    <option value="SYMBIAN">SYMBIAN</option>
		    <option value="MTK">MTK</option>
		    <option value="BLACKBERRY">BLACKBERRY</option>
		</select>	</td>
	</tr>
	<tr>
		<td width="30%" class="title">消息回调地址：</td>
		<td width="70%"><input name="app.callBackUrl" id="callBackUrl"
			type="text"/></td>
	</tr>
	<tr>
		<td class="title">应用简介：</td>
		<td colspan="1"><textarea name="app.appProfile" cols="" rows="16">&nbsp;</textarea></td>
	</tr>
	<tr class="bu_check">
		<td class="title">推送类型：</td>
		<td colspan="1"><label><input name="pushMode"
			type="checkbox" checked="checked" value="one"/>单条(默认)</label><label><input
			name="pushMode" type="checkbox" value="broadcast"/>广播</label><label><input
			name="pushMode" type="checkbox" value="group"/>组播</label></td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息生命周期：</td>
		<td colspan="1"><label><input name="messageLifeCycle"
			type="checkbox" checked="checked" value="default"/>24小时(默认)</label><label><input
			name="messageLifeCycle" type="checkbox" value="custom" id="customs" onselect="showDiv()" onclick="showDiv()"/>自定义</label><label><input
			name="messageLifeCycle" type="checkbox" value="never"/>永久</label>
			<input name="custom" id="custom" maxlength="11" type="hidden" value=""/>
			</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息格式：</td>
		<td colspan="1"><label><input
			name="messageFormat" type="checkbox" checked="checked" value="txt"/>文本</label><label><input 
			name="messageFormat" type="checkbox" value="audio"/>音乐</label><label><input
			name="messageFormat" type="checkbox" value="vedio"/>视频</label><label><input
			name="messageFormat" type="checkbox" value="image"/>图片</label><label><input
			name="messageFormat" type="checkbox" value="net"/>网页</label><label><input
			name="messageFormat" type="checkbox" value="app"/>应用</label><label><input
			name="messageFormat" type="checkbox" value="phone"/>电话</label><label><input
			name="messageFormat" type="checkbox" value="local"/>地理位置</label></td>
	</tr>
	<tr>
		<td class="bu_td" colspan="2"><input type="submit" value="提交申请"
			class="bu_input" /> <input type="button" value="重  置"
			class="bu_input" /></td>
	</tr>
</table>
</form>
</div>
<a  href="#inlineContent" id="divAnchor"  style="display:none"></a>
<div id="inlineContent" style=" display:none;"><!--这是一个弹出消息-->
<div  class="pop_box">
 <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
 <table>
  <tr>
    <th>请输入自定义时效：</th>
    <td><input name="self" id="self" maxlength="11" type="text" value=""/>    
    </td>
  </tr>
  <tr>
    <td colspan="2">
    <input class="kill_box" type="button" value="确 定"/>&nbsp;<input id="kill_box" type="button" value="取消"/>
    </td>
  </tr>
  </table>
</div>
</div>
</body>
</html>