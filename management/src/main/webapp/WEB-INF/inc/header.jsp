<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
Cookie cookies[]=request.getCookies();
String userName="";
if(cookies!=null){
	for(int i=0;i<cookies.length;i++){ 
		Cookie  sCookie=cookies[i]; 
		if(sCookie.getName().equals("loginName")){
			userName=sCookie.getValue();
			break;
		}
	} 
}
String userEmail=(String)request.getSession().getAttribute("userEmail");
%>	

	<div class="header clearfix">

			<div class="logo"></div>

        <div class="header_right">
           <p>
		   <% 
		   	if(userEmail==null || userEmail.equals("")){
				%>
				<%
		  	}else{
				%>
					当前用户：<b><%=userEmail%></b>&nbsp;&nbsp;&nbsp;[<a href="<%=request.getContextPath()%>/view/modifyPwd.jsp">修改密码</a>|<a href="<%=request.getContextPath()%>/view/loginOut.action">退出</a>]
				<%
			}
		   %>
		   
		   
           </p>
           <ul>
           		<li><a class="a_01" href="http://www.hudee.com/" target="_blank"></a>
                </li>
                <li><a class="a_02" href="http://yuntuisong.hudee.com/" target="_blank"></a>
                </li>
                <li><a <% if(request.getParameter("menu").equals("menu03")){
						if(userName==null || userName.equals("")){
					%> class="now_03" <%
					;}else{
					%> class="now_07" <%
					;}
				}else{
						if(userName==null || userName.equals("")){
					%> class="a_03" <%
					;}else{
					%> class="a_07" <%
					;}
				}%>  href="<%=request.getContextPath()%>/forward.action"></a>
                </li>
                <li><a <% if(request.getParameter("menu").equals("menu05")){%> class="now_05" <%;}else{%> class="a_05" <%;}%>  href="<%=request.getContextPath()%>/contact.jsp"></a>
                </li>
                <li><a <% if(request.getParameter("menu").equals("menu06")){%> class="now_06" <%;}else{%> class="a_06" <%;}%>  href="<%=request.getContextPath()%>/help.jsp"></a>
                </li>
           </ul>
        </div>
    </div>
