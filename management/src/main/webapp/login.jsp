<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@include file="head.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link type="text/css" rel="stylesheet" href="<%=contextPath%>/css/style_.css"/>
	<link type="text/css" rel="stylesheet" href="<%=contextPath%>/css/item_.css"/>
	<script type="text/javascript">var contextPath='<%=contextPath%>';</script>	
	<script type="text/javascript" src="<%=contextPath%>/js/public/jquery.js"></script>
	<script type="text/javascript" src="<%=contextPath%>/js/public/jquery.DOMWindow.js"></script>	
	<script type="text/javascript" src="<%=contextPath%>/js/public/common.js"></script>
	<script type="text/javascript" src="<%=contextPath%>/js/login.js"></script>	
	
<title>后台管理</title>
</head>

<body>

<div class="wrap load"> 
  <!--*******header******--> 
  <jsp:include page="/WEB-INF/inc/header.jsp" >
  		<jsp:param name="menu" value="menu03"/>
  </jsp:include>

  <div class="middle clearfix no_bg"> 
     <div class="item">
     	<div class="top">
     	</div>
        <div class="middle_text">
        	<h2>登录</h2>
			<form id="loginForm" name="loginForm" method="post" action="<%=contextPath%>/login.action" >
            
            <p><label for="username">用户名：</label><input type="text" name="user.userEmail" id="userName" maxlength="30" value="${email}" /><b style="color:#f00"></b></p><!--这里的id你可以自己命名，但是前面的的label中“for=""”名字必须统一-->
            <p><label for="password">密&nbsp;&nbsp;&nbsp;&nbsp;码：</label><input type="password" name="user.userPass" id="password" maxlength="14" value="" /><b style="color:#f00"></b></p>
            <p><label for="randImg">验证码：</label><input type="text" name="randImg" id="randImg" maxlength="4" value=""/><b style="color:#f00"></b></p>
            <p><label for="randomImg"></label><img border="1" src="<%=contextPath %>/createImage.action" id="randomImg"/></p>
           
           
           
            <p  class="but_load"><input type="button"  id="loginBtn"/>忘记密码？<a href="javascript:sendMail();" id="findBackw">点此找回</a>
            </p>
            </form>
     	</div>
        <div class="bottom">
           <s:if test="#request.result==0">
             <script type="text/javascript">
				alert("登录失败，用户名或密码错误！");
			</script>
           </s:if>
           <s:elseif test="#request.result==-1">
              <script type="text/javascript">
				alert("请输入正确的验证码！");
				 $("#userName").focus();
			</script>
           </s:elseif>
     	</div>
     </div>
     <div class="right_item">
     	<img src="images/icon04.png"/>
        <p>还没有帐户？立即 <a  href="<%=contextPath%>/register.jsp">免费注册</a></p>
     </div>
  </div>
   <!--*******middle******-->

</div>


<a  style="display:none" id="showMsgLink"></a>
<div style="display:none" id="showMsg">
	<div class="pop_box"  ><!--这是一个弹出消息-->
		<div class="middle_text h_p" >
			<p  style="min-height:60px">我们已经将密码重置链接发送至您的注册邮箱，请点击邮件中的链接完成密码重置。</p>
			<p class="bu">
			<input type="button"  id="closeBtn" />
			</p> 
		</div>
	</div>	
</div>
<!--*******footer******--> 
<jsp:include page="/WEB-INF/inc/footer.jsp" />

<script type="text/javascript">
	$("#userName").focus();
</script>
</body>
</html>
