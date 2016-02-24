<!DOCTYPE html>
<html dir="ltr" lang="zh-CN">
<head>
<meta charset="utf-8" />
<title>easyDialog v2.0 演示与说明</title>
<meta name="keywords" content="弹出层,javascript弹出层,easyDialog,dialog" />
<meta name="description" content="easyDialog是一款体积小巧、使用简便的javascript弹出层组件。" />
<link rel="stylesheet" href="easydialog.css" />
</head>
<body>

<h3>自定义弹出层</h3>

<div class="code_box">
<pre>
&lt;div id="imgBox" style="display:none"&gt;
&nbsp;&nbsp;&lt;img src="test_img.jpg" alt="" /&gt;
&lt;/div&gt;

easyDialog.open({
  container : 'imgBox',
  autoClose : 2000,
  fixed : false
});
</pre>
<div class="con_right">
	<p>自定义弹出层时，需要先将HTML结构添加到页面中，然后设置其隐藏(display:none)，传参必须为id。<br />
		<b>fixed：</b>该参数为false时，弹出窗口为绝对定位(不跟随页面的滚动而滚动)，为true时，弹出窗口为固定定位(跟随页面的滚动而滚动)。
	</p>
	<a href="javascript:void(0)" class="run" id="demoBtn3">运行</a>
</div>
</div>



<div id="imgBox" style="display:none"><img src="test_img.jpg" alt="" /></div>

<script src="easydialog.min.js"></script>
<script>

var $ = function(){
	return document.getElementById(arguments[0]);
};

var btnFn = function( e ){
	alert( e.target );
	return false;
};


$('demoBtn3').onclick = function(){
	easyDialog.open({
		container : 'imgBox',
		autoClose : 2000,
		fixed : false
	});
};




</script>
</body>
</html>
