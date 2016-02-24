<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<title>消息修改</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=basePath %>/css/item_f.css"/>
<script type="text/javascript" src="<%=basePath%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/broadcast.js"></script>
</head>

<body style="background-color: transparent" class="news_push">
<s:if test="#request.result==1">
  <script type="text/javascript">
     alert("信息修改成功!");
  </script>
</s:if>
<div class="content">
<h2>消息修改</h2>
<form action="<%=basePath %>updateMessage.action" id="message_form" method="post">
<table cellspacing="10" cellpadding="0" class="userInfoTable" style="width:600px">
  <tr>
    <td class="title">应用名称：</td>
    <td>
      <s:if test="#request.appList==null">
        <s:select list="#{'-1':'请选择应用'}"></s:select>
      </s:if>
      <s:else>
         <s:select list="#request.appList" name="message.appName" disabled="true" id="app" listKey="id" listValue="appName" value="#request.app.id"  onchange="selects(this.value)" theme="simple" cssClass="inputText130">
         </s:select>
      </s:else>
    </td>
    <td class="title">推送目标：</td>
    <td><select name="message.pushTarget" id="target" class="inputText130"><option>all</option></select>
    </td>
  </tr>
  <tr>
    <td class="title">消息格式：</td>
    <td>
     <s:select list="#request.format" name="message.messageFormat" id="format" listKey="key" listValue="value" value="message.messageFormat"  theme="simple" cssClass="inputText130">
     </s:select>
    </td>
    <td class="title">消息时效：</td>
    <td id="insert">
     <s:select list="#request.life" name="message.messageAge" id="format" listKey="key" listValue="value" value="message.messageAge"  theme="simple" cssClass="inputText130">
     </s:select>
     </td>
  </tr>
  <tr>
    <td class="title">消息内容：</td>
    <td colspan="3"><textarea name="message.messageContent" id="msg" cols="" rows="" class="inputText130" style="width:496px;">${message.messageContent }</textarea></td>
    </tr>
      <tr class="add_bu">
    <td colspan="4" style="text-align:center;"><input type="submit" value="修改" id="bu_broadcast" class="bu_input"/></td>
    </tr>
</table>
</form>
</div>

</body>
</html>