<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="page_box">
	
    <s:if test="pageIndex!=1">
    	<a href="javascript:locate(<s:property value='pageIndex-1'/>);"><span>&lt;上一页</span></a>
    	<em><a href="javascript:locate(1);" class="numbers">1</a></em>
    </s:if>
    <s:else>
    	<span>&lt;上一页</span>
    </s:else>
    
    <s:if test="pageIndex-1>2">
    	<em>...</em>
    </s:if>
    
    <s:if test="pageIndex-1>1">
    	<em><a href="javascript:locate(<s:property value='pageIndex-1'/>);" class="numbers"><s:property value="pageIndex-1"/></a></em>
    </s:if>
    
    <em><s:property value='pageIndex'/></em>

    <s:if test="pageNum-pageIndex>1">
		<em><a href="javascript:locate(<s:property value='pageIndex+1'/>);" class="numbers"><s:property value="pageIndex+1"/></a></em>
	</s:if>
	
    <s:if test="pageNum-pageIndex>2">
    	<em>...</em>
    </s:if>
    
    <s:if test="pageIndex!=pageNum">
    	<em><a href="javascript:locate(<s:property value='pageNum'/>);" class="numbers"><s:property value="pageNum"/></a></em>
    </s:if>
    
    <strong>共<s:property value="pageNum"/>页</strong>
    <s:if test="pageIndex!=pageNum">
    	<a href="javascript:locate(<s:property value='pageIndex+1'/>);"><span>下一页 &gt;</span></a>
    </s:if>
    <s:else>
    	<span>下一页 &gt;</span>
    </s:else>
</div>
<input type="hidden" id="pageIndex" value="<s:property value='pageIndex'/>"/>
<script>
	function autoGo(e){
		var value=document.getElementById("goinput").value;
		document.getElementById("goinput").value=value.replace(/([^\d]|(^[0]$))/g,'');
		if(e.keyCode==13){
			go(e);
		}
	}
	function go(e){
		if(document.getElementById("goinput").value==''){
				return;
		}
		locate(parseInt(document.getElementById("goinput").value));
	}
	
	function locate(pageIndex){
		var location=window.location.toString();
		
		if(location.indexOf("?")==-1){
			window.location=location+"?pageIndex="+pageIndex;
		}else{
			var i=location.indexOf("&pageIndex=");
			var j=location.indexOf("?pageIndex=");
			
			if(i!=-1){
				var str=location.substring(i);
				location=location.replace(str,"&pageIndex="+pageIndex);		
				window.location=location;			
			}else if(j!=-1){
				var str=location.substring(i);
				location=location.replace(str,"?pageIndex="+pageIndex);		
				window.location=location;
			}else{
				window.location=location+"&pageIndex="+pageIndex;
			}
		}
	}
</script>