<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Administratorer</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
</head>
<body>
	<div id="header">
		<ul id="nav">
			<li><a href="${contextRoot}/admin/clients">Klienter</a></li>
			<li><a href="${contextRoot}/admin/users">Administratorer</a></li>
			<li><a href="${contextRoot}/admin/log">Audit Log</a></li>
		</ul>
		<span id="logo">SDM Replikering</span>
	</div>
	<div id="content">
		<h2>Administratorer</h2>
		<a href="${contextRoot}/admin/users/new">Opret ny administrator</a>
		<ul id="users">
			<#list users as user>
			<li><a href="${contextRoot}/admin/users?id=${user.id}">${user.name}</a></li>
			</#list>
		</ul>
	</div>
</body>
</html>
