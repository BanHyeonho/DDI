/**
 *	index.js 
 */
$(document).ready(function () {
	//간편로그인
	$('#KAKAOLoginBtn').on('click', f_kakaoLogin);
	$('#NAVERLoginBtn').on('click', f_naverLogin);
	
	//로그인
	$('#loginBtn').on('click', f_login);
});

//카카오 로그인
var f_kakaoLogin = function(){
	
	location.href = "https://kauth.kakao.com/oauth/authorize"
					+ "?response_type=code"
					+ "&state=" + encodeURI(gv_API_STATE_CODE)
					+ "&client_id=" + gv_KAKAO_REST_API 
					+ "&redirect_uri=" + gv_KAKAO_REDIRECT_URI
					;
}
//네이버 로그인
var f_naverLogin = function(){
	
	location.href = "https://nid.naver.com/oauth2.0/authorize"
					+ "?response_type=code"		
					+ "&state=" + encodeURI(gv_API_STATE_CODE)		
					+ "&client_id=" + gv_NAVER_CLIENT_ID 
					+ "&redirect_uri=" + gv_NAVER_REDIRECT_URI
				;
}

//로그인
var f_login = function(){
	
	if($('#LOGIN_ID').val() == ''){
		gf_toast('아이디를 입력하세요.', 'info');
		$('#LOGIN_ID').focus();
		return;	
	}
	else if($('#PWD').val() == ''){
		gf_toast('비밀번호를 입력하세요.', 'info');
		$('#PWD').focus();
		return;
	}
	
	$.ajax({
		url: '/loginAction'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			LOGIN_ID : $('#LOGIN_ID').val(),
			PWD : gf_securePw( $('#PWD').val() , $('#publicKey').val() )
		}
		, success: function(data){
			
			if(data.result == 'success'){
				location.replace('/');
			}
			//아이디, 비밀번호 오류
			else if( data.result == 'chkIdPwd'){
				gf_toast('아이디 또는 비밀번호가 일치하지 않습니다.', 'info');
				$('#PWD').val('');
				$('#PWD').focus();
			}
			
		}
		, enctype: 'application/x-www-form-urlencoded'
		, contentType: 'application/x-www-form-urlencoded; charset=UTF-8'
		, processData: true
		, cache: false
	});
}