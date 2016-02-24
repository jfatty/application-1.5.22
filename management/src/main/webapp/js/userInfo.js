$(document).ready(function(){
	$('#divAnchor').openDOMWindow({ 
		eventType:'click',
		windowSourceID:'#inlineContent', 
		loader:1, 
		loaderImagePath:'animationProcessing.gif', 
		loaderHeight:16, 
		loaderWidth:17 
	}); 
	$('.example5closeDOMWindow').closeDOMWindow({eventType:'click'}); 
	$("#kill_box").click(function(){$('.example5closeDOMWindow').click();});
	$("#updateBtn").bind('click',updateUser);
	showTips();
});

function updateUser()
{
	if(validate()){
		$("#userForm").submit();
	}else{
		return false;
	}	
}
function showDiv(){
	$("#divAnchor").click();
}

function validate(collection){
	var flag =true;
	if(collection == null){
		collection = $("#companyName,#contactMan,#contactPhone,#userEmail,#address");
	}
	collection.each(function(index){
			var $id=$(this).attr("id");
			switch($id){
				case "companyName":
					if($.trim($(this).val())==""){
						$(this).next("b").html("公司名称不能为空");
						//$(this).focus();
						flag=false;
					}
					break;	
				case "contactMan":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("联系人姓名不能为空");
						//$(this).focus();				
						flag=false;
					}
					break;
				case "userEmail":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("用户邮箱不能为空");
						//$(this).focus();				
						flag=false;
					}
					break;
				case "contactPhone":
					var username=$(this).val();
					if($.trim($(this).val())==""||!Validator.phone.test($(this).val())){
						$(this).next("b").html("请输入规范的电话号码");
						//$(this).focus();				
						flag=false;
					}
					break;
				case "address":
					var username=$(this).val();
					if($.trim($(this).val())=="" ){
						$(this).next("b").html("公司地址不能为空");
						//$(this).focus();				
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
	});
	return flag;
}


function showTips(){
	$("#companyName,#contactMan,#contactPhone,#userEmail,#address").focus(function(){
			if($(this).val()==''){ 
				$(this).next("b").hide();
				$(this).siblings("img").hide();
				$(this).siblings(".tips").show(500);				
			}

	});
	
	$("#companyName,#contactMan,#contactPhone,#userEmail,#address").blur(function(){
			$(this).siblings(".tips").hide();
			validate($(this));
	});
}
