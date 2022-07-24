<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/resources/tld/DDITagLib.tld" prefix="ddi" %>

<c:choose>
	<c:when test='${PAGE_NAME == "setting"}'>
		<jsp:include page="./contents/setting.jsp" />
	</c:when>
	<c:when test='${PAGE_NAME == "news"}'>
		<jsp:include page="./contents/news.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="./contents/setting.jsp" />
		<script src="/resources/viewJs/contents/setting.js?v=${ddi:jsNow()}"></script>
	</c:otherwise>
</c:choose>
<c:if test='${PAGE_NAME != null}'>
<%-- 각 화면 스크립트 --%>
<script src="${jsLink}?v=${ddi:jsNow()}"></script>
</c:if>
