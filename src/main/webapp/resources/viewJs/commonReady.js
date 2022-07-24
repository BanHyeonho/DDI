/**
 * 공통 document ready
 */
$(document).ready(function() {
	var ajax_mask = $('<div>').addClass('ajax_mask');
	$('body').prepend(ajax_mask);
	$(document).ajaxStart(function() {
		$('.ajax_mask').show();
	}).ajaxError(function(a, b, c) {
//		console.error('ajax - error', a, b, c);
	}).ajaxStop(function() {
		$(".ajax_mask").hide();
	}).on('mousedown', '.ajax_mask', function(e) {
		e.preventDefault();
		gf_toast('처리 중 입니다', 'info');
	});
	
	//우클릭막기
	$(document).on('contextmenu', function() {
		  return false;
	});
				
});