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
<title>敏感词管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/style_f.css" />
<link type="text/css" rel="stylesheet" href="<%=path %>/css/item_f.css" />
<script type="text/javascript" src="<%=path %>/js/public/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery_min.js"></script>
<script type="text/javascript" src="<%=path %>/js/jquery.DOMwindow.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$('#divWord').openDOMWindow({ 
		eventType:'click',
		windowSourceID:'#inlineContent', 
		loader:1, 
		//loaderImagePath:'animationProcessing.gif', 
		loaderHeight:16, 
		loaderWidth:17 
	}); 
	$('.example5closeDOMWindow').closeDOMWindow({eventType:'click'}); 
	$("#button_x").click(function(){$('.example5closeDOMWindow').click();});
	$(".button_x").click(function(){
		//
		var w=$("#uword").val().trim();
		w=$.trim(w);
		if(w==null||w=="")
		{
           alert("请输入敏感词");
           $("#uword").focus();
           return;
		}
		else
		{
            $("#wForm").submit();
		}
	});
	 
});
function showWordDiv(wid,obj){
	//$("#myword").append("<input type='text' id='uword' name='uword' value='"+obj+"' />");
	$("#wid").val(wid);
	$("#uword").val(obj);
	$("#divWord").click();
}	 







$(document).ready(function(){
	$('#showMsgLink').openDOMWindow({ 
		width:557,
		height:200,
		eventType:'click', 
		windowSourceID:'#showMsg'
	});	
	$('#closeBtn').closeDOMWindow({
		eventType:'click',
		windowSourceID:'#showMsg'
	}); 	
});
  function checks()
  {
     var word= $("#words").val();
     //word="{word:"+word+"}";
     word=$.trim(word);
     if(word==null||word=="")
     {
        alert("请输入敏感词");
        $("#words").focus();
        return false;
     }
     $.ajax({
			type: "post",
			url: "<%=path%>/ajaxVilidateWords.action",
			data:"word="+word,
			success: function(result) {
				if(result=="1") {
					document.getElementById("result").style.display="";
					return false;
				} 
			else {
				document.getElementById("result").style.display="none";
				   var forms=document.getElementById("word_form");
				   forms.submit();
				}
			},
//			complete: function(XMLHttpRequest, textStatus) {},
//			error: function(xhr, status, error) {
//				alert("服务器出错，请稍候再试。。。"+xhr);
//				//document.getElementById("validation").value = "服务器出错，请稍候再试。。。";
//				return true;
//			},
//				dataType:"xml",
			cache:false
		});

   }
   function sub_mit()
   {
      if(checks())
      {
        
       }
    }

   function update_word(wid,obj)
   {
	   $("#showMsgLink").click();
   }

</script>
</head>
<body style="background-color: transparent" class="app_mana">
<div class="content">
<form action="<%=path %>/addWord.action" method="post" id="word_form">
   <table class="userInfoTable" cellpadding="0" cellspacing="10">
      <tr>
        <td width="10%" align="right" style="text-align:right;">敏感词</td>
        <td width="30%"><input type="text" class="inputText" name="words" id="words"/><font id="result" color="red;" style="color: red;display: none"> 此敏感词已经存在</font></td>
        <td width="*">
         <a href="#" style="float:left;" class="button_box" name="btn_add" id="btn_add"  onclick="checks()">新增</a>
        </td>
      </tr>
   </table>
</form>
<table class="appTable" cellpadding="0" cellspacing="0">
	<tr>
		<th scope="col">序号</th>
		<th scope="col">敏感词</th>
		<th scope="col">操作</th>
	</tr>
	<s:if test="#request.words==null">
	   <tr>
	      <td colspan="4">暂无敏感词
	      </td>
	   </tr>
	</s:if>
	<s:else>
	   <s:iterator value="#request.words" id="word" status="status">
	      <tr>
	          <td><s:property value="#status.index+1"/></td>
	         <td><s:property value="#word.word"/> </td>
	         <td><a href="javascript:if(confirm('确定删除?'))location.href='<%=path %>/deleteWord.action?wordId=<s:property value="#word.id"/>'" target="main">删除</a>
	        <a  href="javascript:this.showWordDiv('<s:property value="#word.id"/>','<s:property value="#word.word"/>');"  class="defaultDOMWindow" >修改</a>
	         </td>
	      </tr>
	   </s:iterator>
	</s:else>
</table>
<div id="page_box" align="right" style="padding-right:20px">
<page:page path="queryWordsList.action" name="pageModel" formName="Form1"
					parameter="page" />
</div>
</div>
<s:if test="#request.result==1&&#request.result!=''">
  <script type="text/javascript">
     alert("新增成功!");
  </script>
</s:if>
<s:if test="#request.result==2&&#request.result!=''">
  <script type="text/javascript">
     alert("修改成功!");
  </script>
</s:if>

<a  href="#inlineContent" id="divWord"  style="display:none"></a>
<div id="inlineContent" style=" display:none;"><!--这是一个弹出消息-->
<div  class="pop_box">
        <a href="#inlineContent" class="example5closeDOMWindow" style="display:none">close aaaaaaaa</a>
        <h2>修改敏感词</h2>
<form action="<%=path%>/updateWord.action" id="wForm" name="wForm" method="post">
			<table>
			   <tr>
			     <td style="text-align:right;padding-right:10px;">敏感词</td>
			     <td id="myword" width="70%">
			        <input type="text" name="uword" id="uword" class="input_h1" value=""/> 
			         <input type="hidden" name="wid" id="wid" value=""/> 
			      </td>
			   </tr>
			   <tr>
			     <td colspan="2" style="text-align:center;">
			        <input class="button_x" type="button" value="确 定"/>
			        &nbsp;<input id="button_x" class="button_x02" type="button" value="取消"/>
			     </td>
			   </tr>
			</table>
			</form>
			
</div> 
</div>
</body>
</html>