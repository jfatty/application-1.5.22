$(document).ready(function(){			
	$('#div_1Link').openDOMWindow({ 
		width:557,
		height:300,
		eventType:'click', 
		windowSourceID:'#div_1'
	});						
	$('#div_1Btn').closeDOMWindow({
		eventType:'click',
		windowSourceID:'#div_1'
	});
	
	$('#div_2Link').openDOMWindow({ 
		width:557,
		height:450,
		eventType:'click', 
		windowSourceID:'#div_2'
	});						
	$('#div_2Btn').closeDOMWindow({
		eventType:'click',
		windowSourceID:'#div_2'
	});
	
	$('#div_3Link').openDOMWindow({ 
		width:650,
		height:600,
		eventType:'click', 
		windowSourceID:'#div_3'
	});						
	$('#div_3Btn').closeDOMWindow({
		eventType:'click',
		windowSourceID:'#div_3'
	});
	
	//$("#registerBtn1").bind('click',registerBtnClick);
	showTips();
});

function registerBtnClick(){
	if(validate()){
		$('#userForm').submit();
		//$("#div_2Link").click();//弹出对话框
	}else{
		return false;
	}
}


function validate(collection){
	var flag =true;
	if(collection == null){
		collection = $("#userEmail,#companyName,#password,#passwordR,#contactMan,#address,#contactPhone,#agree");
	}
	collection.each(function(index){
			var $id=$(this).attr("id");
			switch($id){
				case "userEmail":
					var email=$(this).val();
					if($.trim(email)=="" || !Validator.email.test(email)){
						$(this).next("b").html("邮箱不规范");
						flag=false;
					}else if(checkEmail(email)){
						$(this).next("b").html("该邮箱已被使用");
						flag=false;
					}
					break;
				case "companyName":
					var companyName=$(this).val();
					if($.trim(companyName)=="" ){
						$(this).next("b").html("公司名称不能为空");
						flag=false;
					}
					break;
				case "password":
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("不能为空");						
						flag=false;
					}else if($(this).val().length<6 || !Validator.password.test($(this).val())){
						$(this).next("b").html("6-15位字母、数字、下划线");						
						flag=false;
					}
					break;
				case "passwordR":
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("不能为空");						
						flag=false;
					}else if($(this).val()!=$("#password").val()){
						$(this).next("b").html("密码不一致");					
						flag=false;
					}
					break;
				case "contactMan":
					var contactMan=$(this).val();
					if($.trim(contactMan)=="" ){
						$(this).next("b").html("联系人不能为空");
						flag=false;
					}
					break;
				case "address":
					var address=$(this).val();
					if($.trim(address)=="" ){
						$(this).next("b").html("地址不能为空");
						flag=false;
					}
					break;
				case "contactPhone":
					if($.trim($(this).val())=="" || !Validator.phone.test($(this).val())){
						$(this).next("b").html("请输入规范的手机号");						
						flag=false;
					}
					break;	
				case "agree":
					if(!$(this).attr("checked")){
						alert("请阅读并同意免责申明");
						flag=false;
					}
					break;
				default:
					flag=false;
			}
			if(flag){
				$(this).next("b").hide();
				$(this).siblings("img").show(500);
			}else{
				$(this).siblings("img").hide();
				$(this).next("b").show(500)
				
			}
			//return flag;
	});
	return flag;
}


function checkAppName(){
	var flag = false;
	$.ajax({
		    url:contextPath+"/view/app/appList.action",
			data:"temp="+(new Date()).valueOf(),
			type:"post",
			dataType:"json",
			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
			async:false,
			success:function(data){
				if(data.actionErrors && data.actionErrors.length > 0){
					alert("验证名称出错");
				}else{
					var list=data.appList;
					for(var index in list){
						if(list[index].appName==$("#appName").val()){
							flag=true;
						}
					}
				}			
			},
			error:function(data){flag=true;}
		});
	return flag;

}


function checkUserName(userName){
	var flag = false;
	$.ajax({
		    url:contextPath+"/checkUserName.action",
			data:"userName="+userName+"&temp="+(new Date()).valueOf(),
			type:"post",
			dataType:"json",
			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
			async:false,
			success:function(data){
				if(data.actionErrors && data.actionErrors.length > 0){
					alert("错误的请求");
				}else{
					if(data.isExist=="true"){
						flag=true;
					}
				}			
			}
		});
	return flag;
}

function checkEmail(email){
	var flag = false;
	$.ajax({
		    url:contextPath+"/vilidateEmail.action",
			data:"userEmail="+email+"&temp="+(new Date()).valueOf(),
			type:"post",
			dataType:"json",
			contentType: "application/x-www-form-urlencoded;charset=utf-8", 
			async:false,
			success:function(data){
				if(data.actionErrors && data.actionErrors.length > 0){
					alert("错误的请求");
				}else{
					if(data.isExist=="true"){
						flag=true;
					}
				}			
			}
		});
	return flag;	
	
}

function showTips(){
	$("#userEmail,#companyName,#password,#passwordR,#contactMan,#contactPhone,#address").focus(function(){
			if($(this).val()==''){ 
				$(this).next("b").hide();
				$(this).siblings("img").hide();
				$(this).siblings(".tips").show(500);				
			}

	});
	
	$("#userEmail,#companyName,#password,#passwordR,#contactMan,#contactPhone,#address").blur(function(){
			$(this).siblings(".tips").hide();
			validate($(this));
	});
}

