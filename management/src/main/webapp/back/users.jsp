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
<title>用户列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
   function selectALl()
   {
      var boxs=document.getElementsByTagName("input"); 
      if($("#alls").attr("checked"))
      {
	      for(i = 0;i < boxs.length;i++)
	       { 
	    	  if(boxs[i].type == "checkbox") 
	    	  { 
	    		  boxs[i].checked = true; 
	    	  } 
	    	} 
      }
      else
      {
    	  for(i = 0;i < boxs.length;i++)
	       { 
	    	  if(boxs[i].type == "checkbox") 
	    	  { 
	    		  boxs[i].checked = false; 
	    	  } 
	    	} 
       }
    }

   function setManager()
   {
	   var boxs=document.getElementsByTagName("input"); 
	   var flag=false;
	   for(i = 1;i< boxs.length&&boxs[i].type == "checkbox";i++)
       { 
    	  if(boxs[i].checked) 
    	  { 
        	  
    		  flag=true;
    	  } 
       } 
       if(flag)
       {
           $("#mForm").submit();
        }
       else
       {
           alert("请选择用户");
           return;
        }
   }
</script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<h2>用户列表</h2>
<form action="<%=path %>/setManager.action" method="post" name="mForm" id="mForm">
<table class="table_border">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">用户邮箱</th>
		<th scope="col">
		 <input type="checkbox" value="全选" name="alls" id="alls" onclick="selectALl()"/>
		</th>
	</tr>
	<s:if test="#request.userList==null">
	   <tr>
	      <td colspan="3">暂无用户</td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.userList" id="user" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#user.userEmail"/> </td>
	         <td>
	         <s:if test="#user.roleName=='manager'">
	          <input type="checkbox" name="manager" value="<s:property value="#user.userEmail"/> "  id="all" checked="checked"/>
	         </s:if>
	         <s:else>
	          <input type="checkbox" name="manager" value="<s:property value="#user.userEmail"/> " id="all" />
	         </s:else>
	         </td>
	      </tr>
	   </s:iterator>
	   <tr align="right">
	      <td colspan="3" align="right">
	         <input type="button" value="指定为管理员" onclick="setManager()">
	      </td>
	   </tr>
	</s:else>
</table>
</form>
<br/>
<div id="page_box" align="right">
<page:page path="queryUserList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>
</body>
</html>