<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>管理信息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript" src="<%=path%>/js/userInfo.js"></script>
<script type="text/javascript">
$(function(){
$("#apps").click(function(){
    $("#menu",window.parent.document.body).contents().find("ul li a").removeClass("now");
    $("#menu",window.parent.document.body).contents().find("#a_c").addClass("now");  
	});
$("#messages").click(function(){
    $("#menu",window.parent.document.body).contents().find("ul li a").removeClass("now");
    $("#menu",window.parent.document.body).contents().find("#m_c").addClass("now");  
	});
 })
</script>
</head>
<body style="background-color: transparent" class="info_mana">
<div class="content">
 <center>
    <p>
            最新申请应用：<br/>
           您当前有<font color="red" style="size: 13px;"><span>${appCount}</span> </font>个应用申请需要审核&nbsp;
<a href="<%=path %>/queryCheckAppList.action" id="apps">应用审核</a>
            
    </p>
     <p>
            最新发布消息：<br/>
           您当前有<font color="red" style="size: 13px;"><span>${messageCount}</span> </font>条消息发布需要审核&nbsp;
<a href="<%=path %>/queryCheckMessageList.action" id="messages">消息审核</a>
            
    </p>
 </center>  
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