<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/html;charset=UTF-8");
%> 
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>用户信息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
	<script type="text/javascript" src="<%=path%>/js/public/common.js"></script>
<script type="text/javascript" src="<%=path%>/js/userInfo.js"></script>

</head>
<body style="background-color: transparent" class="info_mana">
<s:if test="#request.result==1">
  <script type="text/javascript">
     alert("信息修改成功!");
  </script>
</s:if>
<div class="content">
<h2>基本信息</h2>
<form action="<%=path %>/updateUser.action" method="post" id="userForm" name="userForm">
<table class="userInfoTable" cellpadding="0" cellspacing="10">
<tr>
	<td class="title">邮件地址：</td>
	<td> <input name="user.userEmail" value="${user.userEmail}"  readonly="readonly" id="userEmail" style="color: red;width: 200px;" class="inputNoBorder" /></td>
	</tr>
	<tr>
		<td class="title">公司名称：</td>
		<td>
		<input name="user.companyName" value="${user.companyName}" class="inputText" id="companyName"/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入您公司名称</b><img
	style="display: none" src="<%=path%>/images/icon_right.gif" />
		</td>
	</tr>
	<tr>
		<td class="title">联系人：</td>
		<td> <input name="user.contactMan" value="${user.contactMan}" id="contactMan" class="inputText"/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入联系人姓名</b><img
	style="display: none" src="<%=path%>/images/icon_right.gif" /></td>
	</tr>
	<tr>
		<td class="title">联系电话：</td>
		<td> <input name="user.contactPhone" value="${user.contactPhone}" id="contactPhone" class="inputText"/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入联系电话</b><img
	style="display: none" src="<%=path%>/images/icon_right.gif" /></td>
	</tr>
	
	<tr>
		<td class="title">公司地址：</td>
		<td> <input name="user.address" value="${user.address}" id="address" class="inputText"/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入您公司地址</b><img
	style="display: none" src="<%=path%>/images/icon_right.gif" /></td>
	</tr>
	<tr>
		<td></td>
		<td class="bu_td"><a class="button_box" id="updateBtn"
			/>保存修改</a><a href="#" id="popup" onclick="javascript:showDiv()" style="display:none">点击弹出div窗口</a>  </td>
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
    <td align="center">您的消息已修改    
    </td>
  </tr>
  <tr>
    <td>
    <input id="kill_box" type="button" value="确定"/>
    </td>
  </tr>
  </table>
</div>
</div>
</body>

</html>