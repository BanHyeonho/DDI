/**
 *	회원가입
 */
$(document).ready(function () {
	if(!confirm('연동된 계정이 없습니다. 회원가입 하시겠습니까?')){
		location.href = '/';
	}
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
	
	$.ajax({
		url: '/oauth/registAction'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			LOGIN_ID : $('#LOGIN_ID').val(),
			USER_NAME : $('#USER_NAME').val(),
			PWD : gf_securePw( String(Math.round(Math.random() * 1000000000)) , $('#publicKey').val() )
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