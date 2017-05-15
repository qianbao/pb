<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<%@ page import="java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>登录页面</title>
</head>
<body>
	<form name="loginForm" method="post" action="pb?m=login">
		<table>
			<tr>
				<td>用户名:<input type="text" name="username" id="username" value="qq773152"></td>
			</tr>
			<tr>
				<td>密码:<input type="password" name="password" id="password" value="2Zhaozibo"></td>
			</tr>
			
			<tr>
				<td>验证码:<input type="text" name="secret" id="secret"> <img id="codeimg" name="codeimg" border=0 src="pb?m=checkcode">
				 <a href="javascript:reloadImage('pb?m=checkcode')">看不清</a><br/>
				</td>
			</tr>
			<tr>
				<td><input type="submit" value="登录"
					style="background-color: pink"> <input type="reset"
					value="重置" style="background-color: red"></td>
			</tr>
		</table>
	</form>
	
	<script language="javascript" type="text/javascript">
      function reloadImage(imgurl){
          var getimagecode=document.getElementById("codeimg");
          getimagecode.src= imgurl + "&id=" + Math.random();
      }
  </script>
</body>
</html>