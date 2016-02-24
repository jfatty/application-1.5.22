<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html>

<head>
<title>消息审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#menu",window.parent.document.body).contents().find("ul li a").removeClass("now");
    $("#menu",window.parent.document.body).contents().find("#m_c").addClass("now");  
	$('#divMessage').openDOMWindow({ 
		eventType:'click',
		windowSourceID:'#inlineContent', 
		loader:1, 
		//loaderImagePath:'animationProcessing.gif', 
		loaderHeight:16, 
		loaderWidth:17 
	}); 
	$('.example5closeDOMWindow').closeDOMWindow({eventType:'click'}); 
	$("#closeBtn").click(function(){$('.example5closeDOMWindow').click();});
	$(".button_x").click(function(){
		//
		$("#check_form").submit();
	});
	 
});
function showMsgDiv(msgId){
	$("#msgId").val(msgId);
	$("#divMessage").click();
}	 
</script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>消息审核列表</h2>
<table class="appTable" cellpadding="0" cellspacing="0">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">应用名称</th>
		<th scope="col">申请时间</th>
		<th scope="col">状态</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.messageList==null">
	   <tr>
	      <td colspan="4">暂无消息</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.messageList" id="msg" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#msg.appName"/> </td>
	         <td>20<s:property value="#msg.createAt"/> </td>
	         <td>
	         <b>
	           <s:if test="#msg.check==1">审核通过</s:if>
	           <s:elseif test="#msg.check==-1">审核不通过</s:elseif>
	           <s:elseif test="#msg.check==-2">广播失败</s:elseif>
	           <s:else>未审核</s:else>
	         </b>
	         </td>
	         <td>
	         <!-- 
	          <a href="<%=path %>/queryMessageById.action?messageId=<s:property value="#msg.id"/>" target="main">详细</a> -->
	         <a href="<%=path %>/queryMessage.action?messageId=<s:property value="#msg.id"/>" target="main">详细</a>
	         <a  href="javascript:this.showMsgDiv('<s:property value='#msg.id'/>');"  class="defaultDOMWindow" >审核</a>
	         
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<div id="page_box" align="right" style="padding-right:20px">
<page:page path="queryCheckMessageList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>

<a  href="#inlineContent" id="divMessage"  style="display:none"></a>
<div id="inlineContent" style=" display:none;" ><!--这是一个弹出消息-->
<div  class="pop_box">
        <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
        <h2>消息审核</h2>
<form action="<%=path %>/checkMessage.action" method="post" id="check_form">
     <table class="alertBox">
       <tr>
         <td colspan="2" class="con_middle">
            <input type="radio" name="message_value" value="1" checked="checked"/>审核通过&nbsp;
            <input type="radio" name="message_value" value="-1"/>未通过&nbsp;
            <input type="hidden" name="msgId" id="msgId" value=""/>
         </td>
         </tr>
       <tr class="button_submit">
         <td colspan="2" align="center">
           <input type="submit" class="button_x" value="提交" id="msgBtn" />&nbsp;
           <input type="reset" value="关闭" id="closeBtn"/>
         </td>
       </tr>
     </table>
  </form>
</div> 
</div>

</body>
</html>