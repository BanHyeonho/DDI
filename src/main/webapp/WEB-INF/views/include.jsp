<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/resources/tld/DDITagLib.tld" prefix="ddi" %>


<!-- Jquery -->
<script src="/resources/script/jquery-3.6.0.min.js?v=${ddi:jsNow()}"></script>
<!-- Jquery-UI -->
<script src="/resources/plugin/jquery-ui-1.13.0/jquery-ui.js?v=${ddi:jsNow()}"></script>
<link  href="/resources/plugin/jquery-ui-1.13.0/jquery-ui.css?v=${ddi:jsNow()}" rel="stylesheet" />

<!-- 암호화 -->
<script src="/resources/script/jsencrypt.min.js?v=${ddi:jsNow()}"></script>

<!-- Jquery.toast -->
<script src="/resources/plugin/jquery-toast/jquery.toast.js?v=${ddi:jsNow()}"></script>
<link  href="/resources/plugin/jquery-toast/jquery.toast.css?v=${ddi:jsNow()}" rel="stylesheet" />

<!-- 사용자정의 -->
<link  href="/resources/css/common.css?v=${ddi:jsNow()}" rel="stylesheet" />
<script src="/resources/script/common.js?v=${ddi:jsNow()}"></script>


<script type="text/javascript">

//사용자id
const gv_commUserId = '${sessionScope.COMM_USER_ID}';
const gv_loginId = '${sessionScope.LOGIN_ID}';

//카카오 간편로그인
const gv_KAKAO_JAVASCRIPT = '${KAKAO_JAVASCRIPT}';
const gv_KAKAO_REST_API = '${KAKAO_REST_API}';
const gv_KAKAO_REDIRECT_URI = location.origin + '${KAKAO_REDIRECT_URI}';

//간편로그인 상태코드
const gv_API_STATE_CODE = '${API_STATE_CODE}';

//네이버 간편로그인
const gv_NAVER_CLIENT_ID = '${NAVER_CLIENT_ID}';
const gv_NAVER_REDIRECT_URI = location.origin + '${NAVER_REDIRECT_URI}';
const gv_NAVER_REDIRECT_URI_LINK = location.origin + '${NAVER_REDIRECT_URI_LINK}';

//간편로그인 타입
const gv_OAUTH_TYPE = '${OAUTH_TYPE}';

</script>

<!-- 공통 document ready -->
<script src="/resources/viewJs/commonReady.js?v=${ddi:jsNow()}"></script>
