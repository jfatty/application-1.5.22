<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="head.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet"
	href="<%=contextPath%>/css/style_.css"/>
<link type="text/css" rel="stylesheet"
	href="<%=contextPath%>/css/item_.css"/>
<script type="text/javascript">var contextPath='<%=contextPath%>';</script>
<script type="text/javascript"
	src="<%=contextPath%>/js/public/jquery.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/js/public/jquery.DOMWindow.js"></script>
<script type="text/javascript"
	src="<%=contextPath%>/js/public/common.js"></script>
<script type="text/javascript" src="<%=contextPath%>/js/register.js"></script>
<title>用户注册</title>
</head>
<body>
<div class="wrap reg"><!--*******header******--> <jsp:include
	page="/WEB-INF/inc/header.jsp">
	<jsp:param name="menu" value="menu03" />
</jsp:include>

<div class="middle clearfix no_bg">

<div class="item">
<div class="top">
<p style="font-style:normal;font-weight:600;"><span>您注册帐户后能申请云推送服务，发送广播消息和查看发送消息状态。</span></p>
</div>
<div class="middle_text style02">
<h2 class="reset">登录</h2>
<form id="userForm" name="userForm" method="post"
	action="<%=contextPath%>/register.action">
<p><span><label for="userEmail">邮箱名称：</label></span><input
	type="text" name="user.userEmail" id="userEmail" maxlength="30" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入您常用邮箱</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="companyName">公司名称：</label></span><input
	type="text" name="user.companyName" id="companyName" maxlength="50" value="" /><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入您公司名称</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="password">密码：</label></span><input
	type="password" name="user.userPass" id="password" maxlength="15" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">6-15位字母、数字、下划线</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="passwordR">确认密码：</label></span><input
	type="password" name="passwordR" id="passwordR" maxlength="15" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">6-15位字母、数字、下划线</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="contactMan">联系人：</label></span><input
	type="text" name="user.contactMan" id="contactMan" maxlength="15" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入联系人</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="address">联系地址：</label></span><input type="text"
	name="user.address" id="address" maxlength="100" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入您公司地址</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p><span><label for="contactPhone">联系电话：</label></span><input
	type="text" name="user.contactPhone" id="contactPhone" maxlength="11" value=""/><b
	style="color: #f00">*</b><b class="tips" style="display: none">请输入手机号码</b><img
	style="display: none" src="<%=contextPath%>/images/icon_right.gif" /></p>
<p class="ps_text">输入手机号是方便我们与您及时联络之用，我们承诺会严格保护您的隐私</p>
<p class="w_input"><input name="agree" id="agree" type="checkbox"/>我已经阅读并同意免责声明
<a href="#div_3" id="div_3Link">查看详细</a>&nbsp;<b style="color: #f00">*</b>
</p>
<p class="but_box"><input type="button" id="registerBtn1" onclick="registerBtnClick()"/>已有帐户？<a
	href="<%=contextPath%>/login.jsp">直接登录</a></p>
</form>
</div>
<div class="bottom"></div>
</div>
<div class="right_item"><img src="images/icon05.png"/>
<p>制作应用很简单，先注册，在制作一下搞定</p>
</div>
</div>
<!--*******middle******--></div>

<div id="div_1" style="display: none">
<div class="pop_box"><!--这是一个弹出消息-->
<div class="middle_text">
<h2>为什么要注册帐户？</h2>
<p>您注册帐户后，能够对您制作的应用进行管理，以及公告发布。同时也方便我们与您联络进行合同审核。</p>
<p class="bu"><input type="button" id="div_1Btn"/></p>
</div>
</div>
</div>

<a href="#div_2" id="div_2Link"></a>
<div id="div_2" style="display: none">
<div class="pop_box"><!--这是一个弹出消息-->
<div class="middle_text">
<p class="ps">请务必确保您的注册信息填写的正确性。
</p>
<p class="bu bu02"><input type="button"
	onclick="javascript:$('#userForm').submit();"/><input
	class="bu03" type="button" id="div_2Btn"/></p>
</div>
</div>
</div>

<div id="div_3" style="display: none">
<div class="pop_box"><!--这是一个弹出消息-->
<div class="middle_text" style="width: 550px">
<h2>免责声明</h2>
<p>免责声明有待补充</p>
<p class="bu"><input type="button" id="div_3Btn"/></p>
</div>
</div>
</div>
<!--*******footer******-->
<jsp:include page="/WEB-INF/inc/footer.jsp" />


</body>
</html>
