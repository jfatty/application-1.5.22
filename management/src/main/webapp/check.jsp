<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib prefix="s" uri="/s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/plain; charset=UTF-8">

<title>应用审核</title>
<script type="text/javascript">
 
   function submit_form(result)
   {
      var forms= document.getElementById("form1");
      var appId=document.getElementById("appId").value;
      forms.action="<%=basePath%>checkApp.action?result="+result+"&appId="+appId;
      forms.submit();
  }
</script>
</head>
<body>
<h2 align="center">应用审核</h2>

<form action="<%=basePath%>checkApp.action" method="post" id="form1" name="form1">
   <table border="1" width="800" align="center">
      <tr>
        <td>应用名称</td>
        <td><input name="appName" id="appName" value="<s:property value='#request.app.appName'/>"><font color="red">*</font> 
            <input type="hidden" id="appId" value="<s:property value='#request.app.id'/>">
        </td>
      </tr>
      <tr>
        <td>所属公司</td>
        <td><input name="appCom" id="appCom" value="<s:property value='#request.app.appCom'/>"><font color="red">*</font> </td>
      </tr>
       <tr>
        <td>消息时长</td>
        <td>
        <s:if test="#request.app.appType==default">
           	 默认
        </s:if>
        <s:else>自定义</s:else>
         </td>
      </tr>
       <tr>
        <td>应用描述</td>
        <td><textarea rows="10" cols="50" name="appDesc" id="appDesc"><s:property value='#request.app.appDesc'/></textarea> </td>
      </tr>
       <tr>
        <td colspan="2" align="center"><input type="button" value="审核通过" onclick="submit_form(1)"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="通过但默认" onclick="submit_form('2')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="reset" value="拒绝通过"> </td>
      </tr>
   </table>
</form>
</body>
</html>
