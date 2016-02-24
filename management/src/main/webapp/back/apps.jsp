<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>应用审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$("#menu",window.parent.document.body).contents().find("ul li a").removeClass("now");
    $("#menu",window.parent.document.body).contents().find("#a_c").addClass("now");  
	$('#divApp').openDOMWindow({ 
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
		
	});
	 
});
function showAppDiv(appId,checkView){
	if(checkView==null)
	{
		checkView="";
		}
	$("#appId").val(appId);
	$("#checkView").val(checkView);
	$("#divApp").click();
}	 
</script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>应用审核</h2>
<table class="appTable" cellpadding="0" cellspacing="0">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">应用名称</th>
		<th scope="col">应用类型</th>
		<th scope="col">申请时间</th>
		<th scope="col">状态</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.appList==null">
	   <tr>
	      <td colspan="5">暂无应用</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.appList" id="app" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#app.appName"/> </td>
	         <td>
	            <s:if test="#app.appType=='yule'">娱乐</s:if>
	            <s:elseif test="#app.appType=='yinyue'">音乐</s:elseif>
	            <s:elseif test="#app.appType=='xiaoshuo'">小说</s:elseif>
	            <s:elseif test="#app.appType=='yingshi'">影视</s:elseif>
	            <s:else>工具</s:else>
	          </td>
	         <td>20<s:property value="#app.createAt"/> </td>
	         <td><b> <s:if test="#app.checkStatus==1">通过</s:if>
	           <s:elseif test="#app.checkStatus==2">通过但默认</s:elseif>
	           <s:elseif test="#app.checkStatus==0">需要审核</s:elseif>
	           <s:else>拒绝</s:else></b></td>
	         <td>
	         <a href="<%=path %>/queryApp.action?appId=<s:property value="#app.id"/>" target="main">详细</a>&nbsp;
	         <a  href="javascript:this.showAppDiv('<s:property value='#app.id'/>','<s:property value='#app.checkView'/>');"  class="defaultDOMWindow" >审核</a>
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<br/>
<div id="page_box" align="right" style="padding-right:20px">
<page:page path="queryCheckAppList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>







<a  href="#inlineContent" id="divApp"  style="display:none"></a>
<div id="inlineContent" style=" display:none;" ><!--这是一个弹出消息-->
<div  class="pop_box">
        <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
        <h2>审核应用</h2>
<form action="<%=path %>/checkPageApp.action" method="post" id="check_form">
     <table class="alertBox">
       <tr>
         <td colspan="2" class="con_middle">
            <input type="radio" name="check_value" value="1" checked="checked"/>通过&nbsp;
            <input type="radio" name="check_value" value="2"/>通过但只保留一天&nbsp;
            <input type="radio" name="check_value" value="-1"/>未通过&nbsp;
            <input type="hidden" name="appId" id="appId" value=""/>
         </td>
         </tr>
         <tr>
         <td style="text-align:right;">审核意见:
         </td>
         <td>
         <textarea name="app.checkView" id="checkView" rows="5" cols="20">&nbsp;
         </textarea>
         </td>
       </tr>
       <tr class="button_submit">
         <td colspan="2" align="center">
           <input type="submit" class="button_x" value="提交" id="appBtn" />&nbsp;
           <input type="reset" value="关闭" id="closeBtn"/>
         </td>
       </tr>
     </table>
  </form>
</div> 
</div>

</body>
</html>