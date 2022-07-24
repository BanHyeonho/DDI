/**
 *	셋팅 
 */
$(document).ready(function () {
	$('#NAVER').on('change', f_change_news_site);
	$('#DAUM').on('change', f_change_news_site);
	$('#GOOGLE').on('change', f_change_news_site);
	
	$('#addBtn').on('click', f_add_keyword);
	$('#keyword').on('keydown', function(e){
		//엔터
		if(e.which == 13){
			e.preventDefault();
			e.stopPropagation();
			$('#addBtn').click();
		}
	});
	
	
	f_search_news_site();
	f_search_keyword();
});
//뉴스사이트 수정
var f_change_news_site = function(me){
	var id = $(me.currentTarget).attr('id');
	var use_yn = $(me.currentTarget).is(':checked') ? '1' : '0';

	$.ajax({
		url: '/ajax'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			QUERY_ID : "api.P_API_NEWS_SITE",
			PARAM : JSON.stringify({
				SITE_CODE : id,
				USE_YN : use_yn	
			})
		}
		, success: function(data){
			f_search_news_site();
			if(use_yn == '1'){
				f_get_news();
			}
		}
	});
	
}
//뉴스사이트 조회
var f_search_news_site = function(){
	$.ajax({
		url: '/ajax'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			QUERY_ID : "api.S_API_NEWS_SITE",
			PARAM : JSON.stringify({
			})
		}
		, success: function(data){
			$('input[type=checkbox][role="switch"]').prop('checked', false);
			
			var result = data.data.filter(x=>x.USE_YN == '1');
			$.each(result, function(idx, item){
				$("#" + item.SITE_CODE).prop('checked', true);
			});
		}
	});
}
//키워드 추가
var f_add_keyword = function(){
	
	$.ajax({
		url: '/ajax'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			QUERY_ID : "api.I_API_NEWS_KEYWORD",
			PARAM : JSON.stringify({
				KEYWORD : $('#keyword').val().trim()
			})
		}
		, success: function(data){
			f_get_news();
			$('#keyword').val('');
			f_search_keyword();
		}
	});
	
}
//키워드 조회
var f_search_keyword = function(){
	$.ajax({
		url: '/ajax'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			QUERY_ID : "api.S_API_NEWS_KEYWORD",
			PARAM : JSON.stringify({
			})
		}
		, success: function(data){
			$('#keyword_list button').remove();			
			var result = data.data;
			$.each(result, function(idx, item){
				
				var btn = $('<button type="button" class="btn keyword-btn me-1 mb-1">').append($('<span>').text(" " + item.KEYWORD + " "));
				btn.append($('<span class="badge bg-secondary" onclick="f_delete_weyword(this);">').text('-'));
				$('#keyword_list').append(btn);
			});
			
		}
	});
}
//키워드 삭제
var f_delete_weyword = function(me){
	
	$.ajax({
		url: '/ajax'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: {
			QUERY_ID : "api.D_API_NEWS_KEYWORD",
			PARAM : JSON.stringify({
				KEYWORD : $(me).siblings('span').text().trim()
			})
		}
		, success: function(data){
			f_search_keyword();
		}
	});
}

//뉴스데이터 가져오기
var f_get_news = function(){
	
	$.ajax({
		url: '/api/news'
		, method: 'post'
		, dataType: 'json'
		, async: true
		, data: null
		, success: null
	});
}
