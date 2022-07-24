<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html>
<main>
    <div class="container-fluid px-4">
        <h1 class="mt-4">뉴스</h1>
        <ol class="breadcrumb mb-4">
            <li class="breadcrumb-item active">뉴스</li>
        </ol>
        
        <div class="card mb-4">
            <div class="card-header">
                <i class="fas fa-table me-1"></i>
                뉴스 데이터
            </div>
            <div class="card-body">
                <table id="datatablesSimple">
                    <thead>
                        <tr>
                            <th width="5%">사이트</th>
                            <th width="5%">키워드</th>
                            <th width="50%">제목</th>
                            <th width="5%">링크</th>
                            <th width="50%">요약</th>
                            <th width="5%">제공시간</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>사이트</th>
                            <th>키워드</th>
                            <th>제목</th>
                            <th>링크</th>
                            <th>요약</th>
                            <th>제공시간</th>
                        </tr>
                    </tfoot>
                    <tbody>
                    <c:forEach var="item" items="${NEWS}">
						<tr>
                            <td>${item.get("SCRAP_SITE")}</td>
                            <td>${item.get("KEYWORD")}</td>
                            <td>${item.get("TITLE")}</td>
                            <td><a href='${item.get("SITE_LINK")}' target="_blank" >${item.get("SITE_LINK")}</a></td>
                            <td>${item.get("DESCRIPTION")}</td>
                            <td>${item.get("PUB_DATE")}</td>
                        </tr>
					</c:forEach>
                        
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>
