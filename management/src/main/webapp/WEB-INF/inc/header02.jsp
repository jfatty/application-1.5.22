<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<div class="header clearfix">
        <div class="logo">
        </div>
        <div class="header_right">
           <p>[<a href="password.shtml">登录</a>|<a href="">注册</a>]
           </p>
           <ul>
           		<li><a class="a_01" href=""></a>
                </li>
                <li><a class="a_02" href=""></a>
                </li>
                <li><a <% if(request.getParameter("menu").equals("menu03")){%> class="now_03" <%;}else{%> class="a_03" <%;}%>  href="#"></a>
                </li>
                <li><a class="a_04" href=""></a>
                </li>
                <li><a class="a_05" href=""></a>
                </li>
                <li><a <% if(request.getParameter("menu").equals("menu06")){%> class="now_06" <%;}else{%> class="a_06" <%;}%>  href="<%=request.getContextPath()%>/help.jsp"></a>
                </li>
           </ul>
        </div>
    </div>
