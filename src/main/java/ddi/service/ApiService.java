package ddi.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import ddi.dao.ComDao;
import ddi.init.InitBean;
import ddi.util.HttpUtil;
import oracle.net.aso.x;

@Service("ApiService")
public class ApiService {
		
	@Autowired
	ComDao comDao;
	
	@Autowired
	AsyncService async;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
	
	/**
	 * 뉴스 데이터 수집
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public void news(HttpServletRequest request){
		async.run(() -> {
			try {
				Map param = new HashMap();
				if(request != null) {
					String userId = String.valueOf(request.getSession().getAttribute("COMM_USER_ID"));
					param.put("COMM_USER_ID", userId);	
				}
				
				List<Map> logs = comDao.selectList("api.S_API_NEWS_LOG", param);
				List<Map> sites = comDao.selectList("api.S_API_NEWS_SITE", param);
				List<Map> keywords = comDao.selectList("api.S_API_NEWS_KEYWORD", param);
				
				for (Map<String, String> site : sites) {
					String siteCode = site.get("SITE_CODE");
					if("NAVER".equals(siteCode)) {
						
						for (int i = 0; i < keywords.size(); i++) {
							
							Map<String, String> keyword = keywords.get(i);
							
							String keywordStr = keyword.get("KEYWORD");
							
							Optional<Map> last = logs.stream().filter(x-> siteCode.equals(x.get("SCRAP_SITE")) && keywordStr.equals(x.get("KEYWORD")) ).findAny();
							Map lastLog = new HashMap();
							if(last.isPresent()) {
								lastLog = last.get();
							}
							
							int idx = 1;
							int totalCnt = naverNews(keywordStr, String.valueOf(idx), lastLog);
							idx += 100;
							
							while(totalCnt > 0
								&& idx <= 1000) {
								totalCnt = naverNews(keywordStr, String.valueOf(idx), lastLog);
								idx += 100;
								if(idx == 1001) {
									idx = 1000;
								}
							}
							
						}
					}
					else if("GOOGLE".equals(siteCode)) {
						for (int i = 0; i < keywords.size(); i++) {
							
							Map<String, String> keyword = keywords.get(i);
							String keywordStr = keyword.get("KEYWORD");
							
							Optional<Map> last = logs.stream().filter(x-> siteCode.equals(x.get("SCRAP_SITE")) && keywordStr.equals(x.get("KEYWORD")) ).findAny();
							Map lastLog = new HashMap();
							if(last.isPresent()) {
								lastLog = last.get();
							}
							
							googleNews(keywordStr, lastLog);
							
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});		
	}
	
	/**
	 * 네이버 뉴스 api
	 * @param keyword
	 * @param start
	 * @param lastLog
	 * @return
	 * @throws Exception
	 */
	public int naverNews(String keyword, String start, Map lastLog) throws Exception {
		int result = 0;
		Map httpParam = new HashMap();
		Map headerParam = new HashMap();
		Map bodyParam = new HashMap();
		
		String url = "https://openapi.naver.com/v1/search/news.json";
		httpParam.put("method", RequestMethod.GET);
		
		headerParam.put("X-Naver-Client-Id", InitBean.getNAVER_CLIENT_ID() );
		headerParam.put("X-Naver-Client-Secret", InitBean.getNAVER_CLIENT_SECRET() );
		
		url = url + "?query=" + URLEncoder.encode(keyword, "UTF-8")	// 검색을 원하는 문자열
					+ "&display=" + "100"							// 검색 결과 출력 건수 지정 / 10(기본값), 100(최대)	
					+ "&start=" + start								// 검색 시작 위치로 최대 1000까지 가능 / 1(기본값), 1000(최대)
					+ "&sort=" + "date";							// 정렬 옵션: sim (유사도순), date (날짜순) / sim, date(기본값)
									
		httpParam.put("url", url);
		
		Map newsResult = HttpUtil.call(httpParam, headerParam, null);

		//성공
		if("200".equals(String.valueOf( newsResult.get("responseCode") ))) {
			Map<String, Object> data = (Map)newsResult.get("data");
			
			result = Integer.parseInt(String.valueOf(data.get("total"))) - Integer.parseInt(start);
			
			List paramList = new ArrayList();
			Map param = new HashMap();
			for (int i = 0; i < ((List)data.get("items")).size(); i++) {
				Map<String, String> item = (Map) ((List)data.get("items")).get(i);
				Map p = new HashMap();
				p.put("SCRAP_SITE", "NAVER");
				p.put("KEYWORD", keyword);
				p.put("TITLE", item.get("title"));
				p.put("SITE_LINK", item.get("link"));
				p.put("ORIGINAL_LINK", item.get("originallink"));
				p.put("DESCRIPTION", item.get("description"));
				p.put("PUB_DATE", new Date(item.get("pubDate")));
				
				if("NAVER".equals(lastLog.get("SCRAP_SITE"))
				&& keyword.equals(lastLog.get("KEYWORD"))
				&& String.valueOf(item.get("originallink")).equals(lastLog.get("ORIGINAL_LINK"))
				&& String.valueOf(item.get("link")).equals(lastLog.get("SITE_LINK"))				
				) {
					result = -1;
					break;
				}
				
				if(i == 0 && "1".equals(start)) {
					insertNews(p);
				}
				else {
					paramList.add(p);	
				}
				
			}
			if(paramList.size() > 0) {
				param.put("LIST", paramList);
				insertNewsMany(param);	
			}
			
		}
		return result;
	}
	
	/**
	 * 구글 뉴스 스크래핑
	 * @param keyword
	 * @param start
	 * @param lastLog
	 * @return
	 * @throws Exception
	 */
	public void googleNews(String keyword, Map lastLog) throws Exception {
		
		Map httpParam = new HashMap();
		
		String url = "https://news.google.com/rss/search";
		httpParam.put("method", RequestMethod.GET);
		
		
		url = url + "?q=" + URLEncoder.encode(keyword, "UTF-8")		// 검색을 원하는 문자열
				+ "&hl=ko&gl=KR&ceid=KR:ko";
									
		httpParam.put("url", url);
		
		Map newsResult = HttpUtil.call(httpParam, null, null);

		//성공
		if("200".equals(String.valueOf( newsResult.get("responseCode") ))) {
			Map<String, Map<String, Map>> data = (Map)newsResult.get("data");
			
			List items = (List)(data.get("rss").get("channel").get("item"));
			
			List paramList = new ArrayList();
			Map param = new HashMap();
			
			for (int i = 0; i < items.size(); i++) {
				Map<String, String> item = (Map) (items).get(i);
				Map p = new HashMap();
				p.put("SCRAP_SITE", "GOOGLE");
				p.put("KEYWORD", keyword);
				p.put("TITLE", item.get("title"));
				p.put("SITE_LINK", item.get("link"));
				p.put("ORIGINAL_LINK", item.get("link"));
				p.put("PUB_DATE", new Date(item.get("pubDate")));
				
				if("GOOGLE".equals(lastLog.get("SCRAP_SITE"))
				&& keyword.equals(lastLog.get("KEYWORD"))
				&& String.valueOf(item.get("originallink")).equals(lastLog.get("ORIGINAL_LINK"))
				&& String.valueOf(item.get("link")).equals(lastLog.get("SITE_LINK"))				
				) {
					
					break;
				}
				
				if(i == 0) {
					insertNews(p);
				}
				else {
					paramList.add(p);	
				}
				
			}
			if(paramList.size() > 0) {
				param.put("LIST", paramList);
				insertNewsMany(param);	
			}
			
		}
		
	}
	
	/**
	 * 데이터 저장
	 * @param p_param
	 */
	public void insertNews(Map p_param){
		
		try {
			comDao.insert("api.I_API_NEWS", p_param);
			comDao.insert("api.I_API_NEWS_LOG", p_param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertNewsMany(Map p_param){
		
		try {
			comDao.insert("api.I_API_NEWS_MANY", p_param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
