<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<%@ page import="java.util.*"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>��¼ҳ��</title>
</head>
<body>
	<form name="loginForm" method="post" action="pb?m=login">
		<table>
			<tr>
				<td>�û���:<input type="text" name="username" id="username" value="qq773152"></td>
			</tr>
			<tr>
				<td>����:<input type="password" name="password" id="password" value="2Zhaozibo"></td>
			</tr>
			
			<tr>
				<td>��֤��:<input type="text" name="secret" id="secret"> <img id="codeimg" name="codeimg" border=0 src="pb?m=checkcode">
				 <a href="javascript:reloadImage('pb?m=checkcode')">������</a><br/>
				</td>
			</tr>
			<tr>
				<td><input type="submit" value="��¼"
					style="background-color: pink"> <input type="reset"
					value="����" style="background-color: red"></td>
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