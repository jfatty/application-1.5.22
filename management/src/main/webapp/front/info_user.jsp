<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
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
<script type="text/javascript" src="<%=path%>/js/userInfo.js"></script>

</head>
<body style="background-color: transparent" class="info_mana">
<s:if test="#request.result=='1'">
  <script type="text/javascript">
  alert("修改成功！");
  </script>
</s:if>
<s:else>
</s:else>
<div class="content">
<h2>基本信息</h2>
<form action="<%=path %>/updateUser.action">
<table>
	<tr>
		<td width="86" class="title">公司名称：</td>
		<td width="662">
		<input name="user.companyName" value="${user.companyName}"/>
		</td>
	</tr>
	<tr>
		<td class="title">联系人：</td>
		<td> <input name="user.contactMan" value="${user.contactMan}"/></td>
	</tr>
	<tr>
		<td class="title">联系电话：</td>
		<td> <input name="user.contactPhone" value="${user.contactPhone}"/></td>
	</tr>
	<tr>
		<td class="title">邮件地址：</td>
		<td> <input name="user.userEmail" value="${user.userEmail}" readonly="readonly"/></td>
	</tr>
	<tr>
		<td class="title">公司地址：</td>
		<td> <input name="user.address" value="${user.address}"/></td>
	</tr>
	<tr>
		<td colspan="2" class="bu_td"><input type="submit" 
			value="修改资料"/><a href="#" id="popup" onclick="javascript:showDiv()" style="display: none;">点击弹出div窗口</a>  </td>
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