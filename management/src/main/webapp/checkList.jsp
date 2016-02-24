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

<title>审核应用</title>
<script type="text/javascript">

</script>
</head>
<body>
<h2 align="center">待审核应用列表</h2>
<table border="1" width="800" align="center">
  <tr align="center">
    <td>应用名称</td>
    <td>选择类型</td>
    <td>审核</td>
  </tr>
  <s:if test="#request.list==null||#request.list.size()==0">
    <tr>
      <td colspan="3" align="center">暂无可审核应用</td>
    </tr>
  </s:if>
  <s:else>
  <s:iterator id="app" value="#request.list" status="st">
  <tr>
    <td><s:property value="appName"/></td>
    <td><s:if test="appType==custom">自定义</s:if><s:else>默认</s:else> </td>
    <td> <a href="<%=basePath %>appInfo.action?appId=<s:property value="id"/>">审核</a></td>
  </tr>
  </s:iterator>
  </s:else>
</table>
</body>
</html>
