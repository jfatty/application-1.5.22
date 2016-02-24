<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
        <div class="sidebar">
            <h2>菜单</h2>
            <h3>应用管理</h3>
                <ul>
                    <li class="icon01"><b></b><a href="<%=request.getContextPath()%>/view/app/create.jsp">制作新应用</a></li>
                    <li class="icon02"><a href="<%=request.getContextPath()%>/view/app/appList.action">应用列表</a></li>
                </ul>
            <h3>公告管理</h3>
                <ul>
                    <li class="now02"><a href="<%=request.getContextPath()%>/view/broadcast/broadcast.jsp">公告发布</a></li>
                    <li class="now03"><a href="<%=request.getContextPath()%>/view/broadcast/broadcastList.action">公告列表</a></li>
                </ul>	
        </div>