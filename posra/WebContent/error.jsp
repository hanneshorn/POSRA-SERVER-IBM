<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error</title>
</head>
<body>

	<h2>There appears to have been an error in processing your
		request. We regret this issue.</h2>
	<p>
		The error was probably something along the lines of this:
		<c:out value="${error}" />
	</p>
	<p>You may return to the main page by way of the back button on
		your browser, or by clicking the return button below:</p>

	<form action='index.html' method='post'>
		<input type='submit' value='Return'>
	</form>

</body>
</html>