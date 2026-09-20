<%@ page isErrorPage="true" %>

<h2>Error Details</h2>

<p>Status Code: ${pageContext.errorData.statusCode}</p>
<p>Request URI: ${pageContext.errorData.requestURI}</p>

<p>Exception Type: ${pageContext.errorData.throwable}</p>

<pre>
<%
Throwable t = pageContext.getErrorData().getThrowable();
if (t != null) {
    t.printStackTrace(new java.io.PrintWriter(out));
}
%>
</pre>