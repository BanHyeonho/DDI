/**
 *	회원가입
 */
$(document).ready(function () {
	$('#registBtn').on('click', regist);
});

var regist = function(){
	
	if($('#LOGIN_ID').val() == ''){
		gf_toast('아이디를 입력하세요.', 'info');
		$('#LOGIN_ID').focus();
		return;	
	}
	else if($('#USER_NAME').val() == ''){
		gf_toast('이름을 입력하세요.', 'info');
		$('#USER_NAME').focus();
		return;
	}
	else if($('#PWD').val() == ''){
		gf_toast('비밀번호를 입력하세요.', 'info');
		$('#PWD').focus();
		return;
	}
	else if($('#PWD').val() != $('#PWD_CHK').val()){
		gf_toast('비밀번호가 일치하지 않습니다.', 'info');
		$('#PWD_CHK').focus();
		return;
	}
	
	$.ajax({
		url: '/registAction'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			LOGIN_ID : $('#LOGIN_ID').val(),
			USER_NAME : $('#USER_NAME').val(),
			PWD : gf_securePw( $('#PWD').val() , $('#publicKey').val() )
		}
		, success: function(data){
			
			if(data.result == 'success'){
				location.replace('/');
			}
			//아이디중복
			else if( data.result == 'duplicatedId'){
				gf_toast('이미 존재하는 아이디 입니다.', 'info');
				$('#LOGIN_ID').focus();
			}
			
		}
		, enctype: 'application/x-www-form-urlencoded'
		, contentType: 'application/x-www-form-urlencoded; charset=UTF-8'
		, processData: true
		, cache: false
	});
	
}