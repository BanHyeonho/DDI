/**
 * 사용자정의 함수
 */
//암호화
function gf_securePw(pw, puk) {	//암호화할 비밀번호 , 공개키

	// 객체 생성
	let crypt = new JSEncrypt();

	// 키 설정
	crypt.setPrivateKey(puk);

	// 암호화
	return crypt.encrypt(pw);
}
/*****************************************************************************************************************************************************************
 * 
 * 토스트
 * type : success, danger, info, 기본
 * 
 *****************************************************************************************************************************************************************/
function gf_toast(text, p_type) {
	var option = {};

	//성공시
	if (p_type == 'success') {
		option = {
			duration: 2000
			, type: p_type
		};
	}
	//에러발생시
	else if (p_type == 'danger') {
		option = {
			sticky: true
			, type: p_type
		};
	}
	//정보알림
	else if (p_type == 'info') {
		option = {
			duration: 3000
			, type: p_type
		};
	}
	else {
		option = {
			duration: 3000
		};
	}
	$.toast(text, option);
}