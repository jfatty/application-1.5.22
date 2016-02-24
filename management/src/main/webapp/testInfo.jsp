<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="page" uri="/WEB-INF/page.tld"%>
<html>

<head>
<title>消息审核</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<style type="text/css">
   body{font-size: 13px}
   .divShow{line-height: 32px;height: 32px;background-color: #eee;width: 280px;padding-left: 10px;}
   .divShow span{padding-left: 50px;}
   .dialog{border: solid 5px #666;position: absolute;display: none;z-index: 101}
   .dialog .title{background-color: #fbaf15;padding: 10px;color: #fff;font-weight: bold;}
   .dialog .title img{float: right;cursor: pointer;}
   .dialog .content{background-color: #fff;padding: 25px;height: 60px;}
   .dialog .content img{float: left}
   .dialog .content span{float: left;padding-top: 10px;padding-left: 10px;}
   .dialog .bottom{text-align: right;padding: 10px 10px 10px 0px;background-color: #eee;}
   .mask{width:100%;height:100%;background-color:#000; position: absolute;top: 0px;left: 0px;filter:alpha(opacity=30);display: none;z-index: 100}
   .btn{border: #666 1px solid;padding: 2px;width: 65px;filter:progid:DXImageTransform.Microsoft.Gradient(GradirentType=0,StartColorStr=#ffffff,EndColorStr=#ECE9D8);}
</style>
<script type="text/javascript">
	$(function(){
		$("#Button1").click(function(){
			$(".mask").show();
			showDialog();
			$(".dialog").show();
		});
		function showDialog()
		{
			var objW=$(window);
			var objC=$(".dialog");
			var mask=$(".mask");
			var brsW=objW.width();
			var brsH=objW.height();
			var sclL=objW.scrollLeft();
			var sclT=objW.scrollTop();
			var curW=objC.width();
			var curH=objC.height();
			//计算对话框居中时的左边距
			var left=sclL+(brsW-curW)/2
			//计算对话框居中时的上边距
			var top = sclT + (brsH-curH)/2
			//设置对话框在页面的位置
			objC.css({"left":5,"top":5,"height":objW.height(),"width":objW.width()-10});
		}

		$(window).resize(function(){
			if(!$(".dialog").is(":visible"))
			{
				return;
			}
			showDialog();
		});
		$(".title img").click(function(){
			$(".dialog").hide();
			$(".mask").hide();
		});
		$("#Button3").click(function(){
			$(".dialog").hide();
			$(".mask").hide();
		});
		$("#Button2").click(function(){
			$(".dialog").hide();
			$(".mask").hide();
			if($("input:checked").length!=0)
			{
				$(".divShow").remove();
			}
		});
	})
</script>
</head>
<body>
<div class="divShow"> 
 <input id="Checkbox1" type="checkbox"/>
 <a href="#">这是一条可以删除的数据</a>
 <span>
 	 <input id="Button1" type="button" value="删除" class="btn"/>
 </span>
</div>
<div class="mask"></div>
<div class="dialog">
   <div class="title">
      <img src="<%=path %>/images/icon06.png" alt="点击可以关闭" />删除时提示
   </div>
   <div class="content">
      <img src="<%=path %>/images/plus.png" alt="" />
      <span>您真的要删除该条记录吗？</span>
   </div>
   <div class="bottom">
     <input type="button" id="Button2" value="确定" class="btn"/>&nbsp;&nbsp;
     <input type="button" id="Button3" value="取消" class="btn"/>
   </div>
</div>
</body>
</html>