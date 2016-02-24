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

<title>通过审核应用</title>
<script type="text/javascript">
</script>
</head>
<body>
<h2 align="center">通过审核应用</h2>

<table border="1" width="400" align="center">
  <tr>
    <td>AppId</td>
    <td>AppKey</td>
  </tr>
  <s:if test="#request.map==null">
   	<tr>
   	  <td colspan="2" align="center"> 暂无通过审核的应用</td>
   	</tr>
  </s:if>
  <s:else>
     <s:iterator value="map" status="st">
        <tr>
          <td><s:property value="key"/> </td>
          <td><s:property value="value"/> </td>
        </tr>
     </s:iterator>
  </s:else>
  
</table>
 <h3 align="center"><a href="appRegister.jsp" >注册应用</a> </h3>
</body>
</html>
