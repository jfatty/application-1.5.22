$(document).ready(function(){
	
	$("#password").keydown(function(event){
		if(event.keyCode == 13) {
			$("#loginBtn").click();
		}
	});
	
	$('#showMsgLink').openDOMWindow({ 
		width:557,
		height:200,
		eventType:'click', 
		windowSourceID:'#showMsg'
	});	
	//找回密码
	$("#findBack").click(function(){
        var email=$("#userName").val();
		if(checkEmail(email))
		{
			alert("该邮箱不存在，请重新输入");
			$("#userName").focus();
			return;
		}
		else
		{
			sendMail();
		}
      });
	//验证码
	$("#randomImg").click(function(){
		document.getElementById("randomImg").src=document.getElementById("randomImg").src+"?rand="+Math.random();
	});
	$('#closeBtn').closeDOMWindow({
		eventType:'click',
		windowSourceID:'#showMsg'
	}); 				   
	$("#loginBtn").click(function(){
		if(validate()){
			$("#loginForm").submit();
		}else{
			return false;
		}
	});
});

function validate(collection){
	var flag =true;
	if(collection == null){
		collection = $("#userName,#password,#randImg");
	}
	collection.each(function(index){
			var $id=$(this).attr("id");
			switch($id){
				case "userName":
					if($.trim($(this).val())==""){
						$(this).next("b").html("用户名不能为空");
						$(this).focus();
						flag=false;
					}
					break;	
				case "password":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("密码不能为空");
						$(this).focus();				
						flag=false;
					}else if($(this).val().length<6 || !Validator.password.test($(this).val()) || username.length<6 || !Validator.userName.test(username)){
						$(this).next("b").html("用户名或密码错误");
						$(this).focus();
						flag=false;
					}
					break;
				case "randImg":
					var randImg=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("请输入验证码");
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

function sendMail(){
	 var email=$("#userName").val();
		if(!checkEmail(email))
		{
			alert("该邮箱不存在，请重新输入");
			$("#userName").focus();
			return;
		}
		else
		{
			$.ajax({
				url:contextPath+"/sendMail.action",
				data:"userName="+$("#userName").val()+"&temp="+(new Date()).valueOf(),
				type:"post",
				contentType: "application/x-www-form-urlencoded;charset=utf-8", 
				async:false,
				beforeSend:function(){
					if($("#userName").val()==""){
						alert("请填写用户名");
						return false;
					}else{
						return true;
					}
				},
				success:function(data){
					if(data.actionErrors && data.actionErrors.length > 0){
						alert("验证名称出错");
					}else{
						$("#showMsgLink").click();
					}			
				},
				error:function(data){alert("发送失败")}
			});
	}
}

function checkEmail(email){
	var flag = false;
	$.ajax({
		    url:contextPath+"/vilidateEmail.action",
			data:"userEmail="+email+"&temp="+(new Date()).valueOf(),
			type:"post",
			dataType:"text",
			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
			async:false,
			success:function(data){
				if(data=="1"){
					flag=true;
				}else{
					flag=false;
				}			
			}
		});
	return flag;	
	
}
