$(document).ready(function(){
	$("#bu_broadcast").click(function(){
		if((validate())&&(checkText())){
			//$("#broadcast_forms").submit();
			document.getElementById("broadcast_forms").submit();
		}else{
			return false;
		}
	});
});

function selects(obj)
{
	if(obj=="-1")
	{
		return;
	}
	$.ajax({
		url:"queryMessageLife.action",
		data:{"appId":obj},
		type:"post",
		//dataType:"json",
		//contentType: "application/x-www-form-urlencoded;charset=utf-8",
		async:false,
		success:function(data)
		{
			$("#life").empty();
			$("#msg_format").empty();
			//("#life").append("<option name='sel' value='0'>请选择时效</option>");
			var strs=data.split("/");
			var str=strs[0].split(",");
			for(var i=0;i<str.length;i++)
			{
				var result=str[i].split(":");
				$("#life").append("<option name='sel' value='"+result[0]+"'>"+result[1]+"</option>");
				
			}
			str=strs[1].split(",");
			for(var i=0;i<str.length;i++)
			{
				var result=str[i].split(":");
				$("#msg_format").append("<option name='sel' value='"+result[0]+"'>"+result[1]+"</option>");
				
			}
			
		},
		error:function(xhr, status, error)
		{
			alert("error"+xhr);
            $("#life").empty();
 			$("#life").append("<option id='se1' value='default'>默认时效</option>").append("<option id='se1' value='default1'>222</option>");
			}
	}); 
}	
	function validate(collection){
		var flag =true;
		if(collection == null){
			collection = $("#app,#target,#msg_format,#life,#msg");
		}
		collection.each(function(index){
				var $id=$(this).attr("id");
				switch($id){
					case "app":
						if($.trim($(this).val())=="-1"){
							alert("请选择应用名称");
							$(this).focus();
							flag=false;
						}
						break;	
					case "target":
						var target=$(this).val();
						if($.trim($(this).val())=="" ){
							alert("请选择推送目标");
							$(this).focus();				
							flag=false;
						}
						break;
					case "msg_format":
						var msg_format=$(this).val();
						if($.trim($(this).val())=="" ){
							alert("请选择消息格式");
							$(this).focus();				
							flag=false;
						}
						break;
					case "life":
						var life=$(this).val();
						if($.trim($(this).val())=="" ){
							alert("请选择推送时效");
							$(this).focus();				
							flag=false;
						}
						break;	
					case "msg":
						var msg=$(this).val();
						if($.trim($(this).val())=="" ){
							alert("请输入消息内容");
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

	function checkText()
	{
		var flag=false;
		var msg=$("#msg").val();
		$.ajax({
			url:"ajaxCheckText.action",
			data:{"msg":msg},
			type:"post",
			async:false,
			success:function(data)
			{
				if(data=="1")
				{
					alert("所发消息存在敏感词");
					$("#msg").focus();
					flag=false;	
				}
				else
				{
					flag=true;

				}
			},
			error:function(xhr, status, error)
			{
				alert("error= "+error);
				flag=false;
			}
		}); 
		return flag;
	}