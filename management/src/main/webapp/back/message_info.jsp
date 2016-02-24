<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>消息详细信息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/item_f.css"/>
<script type="text/javascript" src="<%=basePath%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/broadcast.js"></script>
 <script type="text/javascript" src="<%=path%>/js/app.js"></script>
</head>

<body style="background-color: transparent" class="news_push">
        <div class="content">
        <h2>发送消息</h2>
<form action="<%=basePath %>checkMessage.action" id="message_form" method="post">
<table border="1" align="left" width="100%">
  <tr align="left">
    <td width="25%" class="title" align="left">应用名称：</td>
    <td width="75%">
    <input type="text" name="message.appName" value="${message.appName }" readonly="readonly"/> 
    </td>
    </tr>
    <tr>
    <td width="40%" class="title">推送目标：</td>
    <td width="60%">
      <input type="text" name="message.pushTarget" value="${message.pushTarget }" readonly="readonly"/>
    </td>
  </tr>
  <tr>
    <td class="title">消息格式：</td>
    <td>
    <input type="text" name="message.messageFormat" value="${message.messageFormat }" readonly="readonly"/>
    </td>
    </tr>
    <tr>
    <td class="title">消息时效：</td>
    <td id="insert"><input type="text"  value="${message.messageAge }" readonly="readonly"/> </td>
  </tr>
  <tr>
    <td class="title">消息内容：</td>
    <td ><textarea name="msg" id="msg" cols="" rows=""  readonly="readonly">${message.messageContent }&nbsp;</textarea></td>
    </tr>
      <tr class="add_bu">
    <td colspan="2" style="text-align:center;">
    <input type="button" value="审核通过" id="bu_broadcast" onclick="check_message('1')" class="bu_input"/>&nbsp;
    <input type="button" value="审核不通过" id="bu_broadcast" onclick="check_message('-1')" class="bu_input"/>
    <input type="hidden" id="message_value" name="message_value" value=""/>
    </td>
    </tr>
</table>
</form>
</div>
</body>
</html>