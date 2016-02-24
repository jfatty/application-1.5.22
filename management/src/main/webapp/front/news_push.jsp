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
    <td width="22%">
    <s:if test="#request.appList==null">
     <select name="appName" id="app"><option value="-1">请选择应用</option></select>
    </s:if>
    <s:else>
    <s:select list="#request.appList" name="appName" id="app" listKey="id" listValue="appName" value="id" headerKey="-1" headerValue="请选择应用" onchange="selects(this.value)" theme="simple">
    
    </s:select>
    </s:else>
    </td>
    <td width="22%" class="title">推送目标：</td>
    <td width="39%"><select name="target" id="target"><option>all</option></select></td>
  </tr>
  <tr>
    <td class="title">消息格式：</td>
    <td><select name="msg_format" id="msg_format"><option>文本</option></select></td>
    <td class="title">消息时效：</td>
    <td id="insert"><select name="life" id="life">
    <option id="se" value="default">请选择时效</option>
    </select> </td>
  </tr>
  <tr>
    <td class="title">消息内容：</td>
    <td colspan="3"><textarea name="msg" id="msg" cols="" rows="">&nbsp;</textarea></td>
    </tr>
      <tr class="add_bu">
    <td colspan="4"><img src="<%=basePath %>/images/pics.png"/><a href="#">添加图片</a><img src="<%=basePath %>/images/sound_bu.png"/><a href="#">添加音频</a><img src="<%=basePath %>/images/vedio.png"/><a href="#">添加视频</a><img src="<%=basePath %>/images/plus.png"/><a href="#">添加URL</a></td>
    </tr>
      <tr class="add_bu">
    <td colspan="4" style="text-align:center;"><input type="button" value="提 交" id="bu_broadcast" class="bu_input"/></td>
    </tr>
</table>
</form>
</div>
</body>
</html>