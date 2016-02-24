<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/plain; charset=UTF-8">

<title>应用注册</title>
<script type="text/javascript">
   function check()
   {
      var appName=document.getElementById("appName").value;
      var appCom=document.getElementById("appCom").value;
      //var time;//=document.getElementById("messageTime").value;
     // alert("appName="+appName+" appCom="+appCom +" time="+time);
      if(appName==""||appName==null)
      {
          return false;}
      if(appCom==""||appCom==null)
      {
          return false;}
      return true;
      
	}

	function checkChoise(obj)
	{
		for(var i=0;i<document.all(obj).length;i++)
		{
			if(document.all(obj)(i).checked){
				return true;     
				}     
			}
		return false;
	     }
   
   function submit_form()
   {
     if(check()&&checkChoise("messageTime"))
     {
           document.getElementById("form1").submit();
      } 
  }
</script>
</head>
<body>
<h2 align="center">应用注册</h2>

<form action="<%=basePath%>registerApp.action" method="post" id="form1" name="form1">
   <table border="1" width="800" align="center">
      <tr>
        <td>应用名称</td>
        <td><input name="appName" id="appName"><font color="red">*</font> </td>
      </tr>
      <tr>
        <td>所属公司</td>
        <td><input name="appCom" id="appCom"><font color="red">*</font> </td>
      </tr>
       <tr>
        <td>消息时长</td>
        <td>
          <input name="messageTime" type="radio" id="msg1" value="default" checked="checked">默认 <br>
           <input name="messageTime" type="radio" id="msg2" value="custom">自定义 <br>          
         </td>
      </tr>
       <tr>
        <td>应用描述</td>
        <td><textarea rows="10" cols="50" name="appDesc" id="appDesc"></textarea> </td>
      </tr>
       <tr>
        <td colspan="2" align="center"><input type="button" value="提交" onclick="submit_form()">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="reset" value="重置">  </td>
      </tr>
   </table>
</form>
</body>
</html>
