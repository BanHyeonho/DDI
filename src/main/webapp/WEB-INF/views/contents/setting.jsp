<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<style type="text/css">
	.keyword-btn{
		border: 1px solid #ced4da;
		cursor: default !important;
	}
	.keyword-btn>span{
		cursor: pointer !important;
	}
	
</style>
<main>
    <div class="container-fluid px-4">
        <h1 class="mt-4">설정</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">설정</li>
        </ol>
        
        
        <div class='row'>
	        <div class="col-2 text-end">
	        	뉴스 사이트 :
		    </div>
        	<div class="col-10">
				<div class="form-check form-switch form-check-inline">
				  <input class="form-check-input" type="checkbox" role="switch" id="NAVER">
				  <label class="form-check-label" for="NAVER">네이버</label>
				</div>
				<div class="form-check form-switch form-check-inline">
				  <input class="form-check-input" type="checkbox" role="switch" id="GOOGLE">
				  <label class="form-check-label" for="GOOGLE">구글</label>
				</div>
		    </div>
        </div>
        
        <div class='row'>
	        <div class="col-2 text-end">
	        	수집 키워드 :
		    </div>
        	<div class="col-5">
        	
		        <div class="input-group mb-3">
				  <input type="text" class="form-control" placeholder="수집키워드를 입력하세요. 예)선거, 코로나 등" aria-label="keyword" aria-describedby="keyword" id='keyword'>
				  <button class="btn btn-outline-secondary" type="button" id="addBtn">+추가</button>
				</div>
		    </div>
        </div>
        <div class='row'>
        	<div class="col-2">
	        	
		    </div> 
		    <div class="col col-10" id='keyword_list'>
		    
		    </div>
        </div>
    </div>
</main>
