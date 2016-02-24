<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%
  String path=request.getContextPath();
%>
<html>

  <head>
     <title>应用注册</title>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
     <link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
	 <link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
	 <script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
	 <script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
	 <script type="text/javascript" src="<%=path%>/js/app.js"></script>
	<script type="text/javascript">

      $(function(){
		$("#myCustom").click(function(){
			if(this.checked)
			{
				$("#custom_my").css("display","");
			}
			else
			{
				$("#self").val("0");
				$("#custom_my").css("display","none");
			}
		});
      })
	
	   var flag=true;
	   function sel(obj)
	   {
		  if(obj=="custom"&&flag)
		  {
			flag=false;
		  	document.getElementById("custom_my").style.display="";
		  }
		  else if(obj=="custom"&&!flag)
		  {
			  flag=true;
			  document.getElementById("custom_my").style.display="none";
		  }
	   }

       function validateCount()
       {
    	   var flag = false;
    	   $.ajax({
   		    url:"<%=path%>/ajaxCount.action",
   			type:"post",
   			dataType:"json",
   			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
   			async:false,
   			success:function(data){
   	   			if(data=="0")
   	   			{
   	   	   			alert("对不起,您不再允许申请应用,请联系应用开发商");
   	   	   			flag=false;
   	   	   		}
   	   			else
   	   			{
   	   	   			flag=true;
   	   	   		}
   				},
   			error:function(data){flag=false;}
   		});
   		return flag;
       }
	   
	   function validateAppForm()
	   {
	   	var flag =true;
	    if($.trim($("#appName").val())==""){
	   		//$("#appName").next("b").html("应用名不能为空");
	   		alert("应用名不能为空");
	   		$("#appName").focus();
	   		flag=false;
	   	}else if($.trim($("#packageName").val())=="" ){
	   		//$("#packageName").next("b").html("包名不能为空");
	   		alert("包名不能为空");
	   		$("#packageName").focus();				
	   		flag=false;
	   	}else if($.trim($("#callBackUrl").val())=="" ){
		   	alert("回调地址不能为空");
	   		//$("#callBackUrl").next("b").html("回调地址不能为空");
	   		$("#callBackUrl").focus();				
	   		flag=false;
	   	}
	   	else{
	   		flag=true;
	   	}
	   	flag=validateCount();
	   	return flag;	
	   }
	   
	   function submit_app()
	   {
		 // var a=$("#pushMode").find(":checkbox:checked").length;  
	      var val=$("#custom_my").css("display");
	      if(val!="none")
	      {
             var custom=$("#self").val();
             if(!(/^(\+|-)?\d+$/.test(custom))|| custom<1||custom>3650)
             {   
                alert("请输入正确的数字"); 
                $("#self").focus();
                return;  
              } 
             else
             {  
                 if(validateAppForm())
                 {
                   $("#appForm").submit();
                  }
               }                 
		  }
	      else
          {  
              if(validateAppForm())
              {
                $("#appForm").submit();
               }
            }     
	   }
	 </script>
  </head>
 
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>申请AppID、Key</h2>
<form action="<%=path%>/addApp.action" method="post" name="appForm" id="appForm">
<table class="userInfoTable" cellpadding="0" cellspacing="10">
	<tr>
		<td class="title">应用名称：</td>
		<td><input name="app.appName" id="appName" type="text" class="inputText"/><b style="color: #f00">*</b> </td>
	</tr>
	<tr>
		<td class="title">应用类型：</td>
		<td>	
		<select name="app.appType" id="appType" class="inputText">
		   <option value="yule">娱乐</option>
		   <option value="yinyue">音乐</option>
		   <option value="xiaoshuo">小说</option>
		   <option value="yingshi">影视</option>
		   <option value="工具">工具</option>
		</select>	</td>
	</tr>
	<tr>
		<td class="title">包名：</td>
		<td><input name="app.packageName" id="packageName" type="text" class="inputText"/><b
	style="color: #f00">*</b></td>
	</tr>
	<tr>
		<td class="title">推送平台：</td>
		<td><select name="app.platForm" id="platForm" class="inputText">
		    <option value="ANDROID">ANDROID</option>
		    <option value="MTK">MTK</option>
		</select>	</td>
	</tr>
	<tr>
		<td class="title">消息回调地址：</td>
		<td><input name="app.callBackUrl" id="callBackUrl" type="text" class="inputText"/><b
	style="color: #f00">*</b></td>
	</tr>
	<tr>
		<td class="title">应用简介：</td>
		<td><textarea name="app.appProfile" cols="" rows="16" class="inputText">&nbsp;</textarea></td>
	</tr>
	<tr class="bu_check">
		<td class="title">推送类型：</td>
		<td>
		<input type="checkbox" name="app.pushMode" value="one" checked="checked" disabled="disabled"/><span style="color:#999">单条</span>
		<input type="checkbox" name="app.pushMode" value="group" disabled="disabled"/>组播
		<input type="checkbox" name="app.pushMode" value="broadcast" />广播
		<!-- 
		<s:checkboxlist name="app.pushMode" id="pushMode" value="{'one'}"  theme="simple" list="#{'one':'单条','broadcast':'广播','group':'组播'}"></s:checkboxlist>
		 -->
		</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息生命周期：</td>
		<td><label>
		<input type="checkbox" name="app.messageLifeCycle" value="default" checked="checked" disabled="disabled"/><span style="color:#999">1天</span>
		<input type="checkbox" name="app.messageLifeCycle" value="custom" id="myCustom" />自定义
		<input type="checkbox" name="app.messageLifeCycle" value="never"/>永久
			<!--  
		  <s:checkboxlist name="app.messageLifeCycle" id="messageLifeCycle" theme="simple" value="{'default'}" onclick="sel(this.value)"  list="#{'default':'1天','custom':'自定义','never':'永久'}"></s:checkboxlist>
		  -->
		</label>
	</td>
	</tr>
	<tr id="custom_my" style="display: none">
		<td class="title">请输入自定义生命周期：</td>
		<td>
		  <input type="text" value="0" name="self" id="self" class="inputText"/>
		</td>
	</tr>
	<tr class="bu_check">
		<td class="title">消息格式：</td>
		<td>
		<input type="checkbox" name="app.messageFormat" value="text" checked="checked" disabled="disabled"/><span style="color:#999">文本</span>
		<input type="checkbox" name="app.messageFormat" value="audio" />音乐
		<input type="checkbox" name="app.messageFormat" value="vedio" />视频
		<input type="checkbox" name="app.messageFormat" value="image" />图片
		<input type="checkbox" name="app.messageFormat" value="net" />网页<br/>
		<input type="checkbox" name="app.messageFormat" value="app" />应用
		<input type="checkbox" name="app.messageFormat" value="phone" />电话
		<input type="checkbox" name="app.messageFormat" value="local" />地理位置

		<!--  
		<label><s:checkboxlist name="app.messageFormat" id="messageFormat" value="{'text'}"  theme="simple" list="#{'text':'文本','audio':'音乐','vedio':'视频','image':'图片','net':'网页','app':'应用','phone':'电话','local':'地理位置'}"></s:checkboxlist></label>
		-->
		</td>
	</tr>
	<tr>
		<td class="bu_td" colspan="2"><input type="button" value="提交申请"
			class="bu_input" onclick="submit_app()"/> <input type="reset" value="重  置"
			class="bu_input" /></td>
	</tr>
</table>
</form>
</div>
</body>
</html>