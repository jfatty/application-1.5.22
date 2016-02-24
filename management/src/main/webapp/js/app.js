$(document).ready(function(){
	$('#divAnchor').openDOMWindow({ 
		eventType:'click',
		windowSourceID:'#custom_self', 
		loader:1, 
		loaderImagePath:'animationProcessing.gif', 
		loaderHeight:16, 
		loaderWidth:17 
	}); 
	$('.example5closeDOMWindow').closeDOMWindow({eventType:'click'}); 
	$("#kill_box").click(function(){$('.example5closeDOMWindow').click();});
	$(".kill_box").click(function(){
		var s=$("#self").val();
		if(s==null||s=="")
		{
			alert("请输入自定义时效");
			$("#self").focus();
			return;
		}
		else
		{
			$("#custom").val(s);
			//document.getElementById("selfs").value=s;
			alert($("#custom").val());
			$('.example5closeDOMWindow').click();
		}
	});
});
function showDiv(){
	var c=document.getElementById("customs");
	if(c.checked)
	{
		$("#divAnchor").click();
	}
}


function check_app(obj)
{
   $("#check_value").val(obj);
  $("#check_form").submit();;

}

function check_message(obj)
{
  $("#message_value").val(obj);
  $("#message_form").submit();

}

function submit_apps()
{
   var val=$("custom_my").css("display");
}

function validateAppForm()
{

	var flag =true;
	if(collection == null){
		collection = $("#appName,#packageName,#callBackUrl");
	}
	collection.each(function(index){
			var $id=$(this).attr("id");
			switch($id){
				case "appName":
					if($.trim($(this).val())==""){
						$(this).next("b").html("应用名不能为空");
						$(this).focus();
						flag=false;
					}
					break;	
				case "packageName":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("报名不能为空");
						$(this).focus();				
						flag=false;
					}
					break;
				case "callBackUrl":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("回调地址不能为空");
						$(this).focus();				
						flag=false;
					}
					break;
				default:
					flag=false;
			}
			return flag;
	});
	return flag;
	
}