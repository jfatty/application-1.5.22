<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%
  String path=request.getContextPath();
%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<title>Licence管理信息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path%>/css/style_f.css"/>
<link type="text/css" rel="stylesheet" href="<%=path%>/css/item_f.css"/>
<script type="text/javascript" src="<%=path%>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
   $(function(){
	   $("#importFile").click(function(){
		   var obj=$("#licenceFile").val();
           if(obj!=null&&obj!=""){
				var str=obj.substring(obj.lastIndexOf(".")+1,obj.length);
				if(str!=null&&str=="dat")
				{
					$("#fileForm").submit();
				}
				else
				{
					alert("请选择正确文件");
					$("#licenceFile").focus();
				}
             }
           else
           {
          	 alert("请选择文件");
          	$("#licenceFile").focus();
           }
		   });
	   })
</script>
<s:if test="#request.result==1">
  <script type="text/javascript">
     alert("Licence导入成功!");
  </script>
</s:if>
<s:elseif test="#request.result==0&&#request.result!=''">
<script type="text/javascript">
     alert("Licence导入失败!");
  </script>
</s:elseif>
<s:else>

</s:else>
</head>
<body style="background-color: transparent" class="info_mana02">
<div class="content">
<form action="<%=path %>/importLicenceFile.action" method ="post" id="fileForm" enctype ="multipart/form-data">

          <h3>系统信息</h3>

     <p>OPPO后台管理系统</p>
     <p> 系统授权审核应用数:${count }个</p>
            <p> 当前已使用:${app }个&nbsp;&nbsp;剩余：${last }</p>
       <p class="p_02"><b>导入新的licence　</b><input type="file" name="licenceFile" id="licenceFile" /><input class="button_box02" type="button" id="importFile" value="导入"/></p>

 </form>
</div>
<a  href="#inlineContent" id="divAnchor"  style="display:none"></a>
<div id="inlineContent" style=" display:none;"><!--这是一个弹出消息-->
<div  class="pop_box">
 <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
 <table>
  <tr>
    <td align="center">您的消息已修改    
    </td>
  </tr>
  <tr>
    <td>
    <input id="kill_box" type="button" value="确定"/>
    </td>
  </tr>
  </table>
</div>
</div>
</body>

</html>