<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<script type="text/javascript"
	src="<%=basePath%>/js/jquery-3.1.1.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	<h1 style="text-align: center">这是一个Netty5连接服务端的程序</h1>
	<br>
	<textarea style="width: 200px; height: 200px" id="message">
</textarea>
	<input type="button" value="发送一个数据到服务器" id="btn"/>
	<br>
	<label id="recive" style="color: red">接收的数据为</label>
</body>
</html>

<script type="text/javascript">
$(function(){
	$("#btn").click(function() {
		var u="${pageContext.request.contextPath}";
		var url=u+"/netty";
		//alert(url);
		var ax = $.ajax({
			url : url,
			type : "POST",
			data :{"name":$("#message").val()},
			dataType : "json",
			cache : false,
			timeout : 30000,
			async:true,
			beforeSend : function() {
				//alert("beforeSend...");
			},
			error : function(data, textStatus, e) { //出错处理
				alert("error"+":"+textStatus+"            data:"+data);
			},
			success : function(data, textStatus,args) { //成功处理
				$("#recive").html(data.msg);
			}
		});
	});
	
});
</script>
