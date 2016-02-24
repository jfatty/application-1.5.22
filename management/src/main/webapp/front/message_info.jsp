<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>消息推送</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/item_f.css"/>
<script type="text/javascript" src="<%=basePath%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/broadcast.js"></script>
</head>

<body style="background-color: transparent" class="news_push">
        <div class="content">
        <h2>发送消息</h2>
<form action="<%=basePath %>broadcast.action" id="broadcast_form" method="post">
<table>
  <tr>
    <td width="17%" class="title">应用名称：</td>
    <td width="22%">aaaaa   
    </td>
    <td width="22%" class="title">推送目标：</td>
    <td width="39%">aaa
  </tr>
  <tr>
    <td class="title">消息格式：</td>
    <td>文本
    <td class="title">消息时效：</td>
    <td id="insert">默认 </td>
  </tr>
  <tr>
    <td class="title">消息内容：</td>
    <td colspan="3"><textarea name="msg" id="msg" cols="" rows="">&nbsp;</textarea></td>
    </tr>
      <tr class="add_bu">
    <td colspan="4" style="text-align:center;"><input type="button" value="返回" id="bu_broadcast" class="bu_input"/></td>
    </tr>
</table>
</form>
</div>
</body>
</html>