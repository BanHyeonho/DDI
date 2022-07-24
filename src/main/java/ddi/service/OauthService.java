package ddi.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;

import ddi.dao.ComDao;
import ddi.init.InitBean;
import ddi.util.ComUtil;
import ddi.util.HttpUtil;
import ddi.util.ModifiableRequest;

@Service("OauthService")
public class OauthService {
	
	//간편로그인
	@Value("#{commonConfig['KAKAO_REDIRECT_URI']}")
	private String KAKAO_REDIRECT_URI;
	@Value("#{commonConfig['NAVER_REDIRECT_URI']}")
	private String NAVER_REDIRECT_URI;
	
	@Autowired
	ComService comService;
	
	@Autowired
	ComDao comDao;
	
	private static final Logger logger = LoggerFactory.getLogger(OauthService.class);
		
	
	/**
	 * 간편 회원가입처리
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public String registAction(HttpServletRequest request) throws Exception{
		
		String registResult = comService.registAction(request);
		
		//간편로그인 계정 연동
		if("success".equals(registResult)) {
			HttpSession session = request.getSession();
			
			String commUserId = String.valueOf(session.getAttribute("COMM_USER_ID"));
			String oauthType = String.valueOf(session.getAttribute("OAUTH_TYPE"));
			String socialId = String.valueOf(session.getAttribute("SOCIAL_ID"));
			
			Map oauthParam = new HashMap();
			oauthParam.put("OAUTH_TYPE", oauthType);
			oauthParam.put("SOCIAL_ID", socialId);
			oauthParam.put("COMM_USER_ID", commUserId);
			oauthParam.put("CID", commUserId);
			oauthParam.put("CIP", ComUtil.getAddress(request));
			comDao.insert("oauth.I_COMM_OAUTH", oauthParam);
			
			//간편회원가입으로 가입한경우 패스워드 사용안함.
			Map pwdParam = new HashMap();
			pwdParam.put("COMM_USER_ID", commUserId);
			pwdParam.put("PWD_USE_YN", '0');
			pwdParam.put("MID", commUserId);
			pwdParam.put("MIP", ComUtil.getAddress(request));
			comDao.insert("com.U_COMM_USER_PWD_USE", pwdParam);
		}
		
		
		return registResult;
	}
	
	/**
	 * 간편로그인 처리
	 * @param p_type
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String login(String p_type, HttpServletRequest request) throws Exception{
		
		//1.토큰받기
		Map token = getToken(p_type, request);
		String access_token = (String)token.get("access_token");
				
		if(access_token == null) {
//			throw new Exception("토큰에러");
			logger.error("토큰에러");
			return "/";
		}
		//2.사용자 정보받기
		Map userInfo = getUserInfo(p_type, access_token);
		if(userInfo == null) {
//			throw new Exception("사용자 정보에러");
			logger.error("사용자 정보에러");
			return "/";
		}
		Map oauthParam = new HashMap();
		
		oauthParam.put("OAUTH_TYPE", p_type);
		
		String socialId;
		String expiresIn;
		String refreshToken;
		String reTokenExpiresIn = null;
		switch (p_type) {
		case "KAKAO":
			socialId = String.valueOf(userInfo.get("id"));
			
			expiresIn = String.valueOf(token.get("expires_in")); 
			refreshToken = String.valueOf(token.get("refresh_token")); 
			reTokenExpiresIn = String.valueOf(token.get("refresh_token_expires_in")); 
			break;
			
		case "NAVER":
			socialId = String.valueOf( ((Map)userInfo.get("response")).get("id") );
			
			expiresIn = String.valueOf(token.get("expires_in")); 
			refreshToken = String.valueOf(token.get("refresh_token")); 
			
			break;
			
		default:
			throw new Exception("등록되지않은 간편로그인");
		}
		
		//토큰저장
		Map saveTokenParam = new HashMap();
		saveTokenParam.put("OAUTH_TYPE", p_type);
		saveTokenParam.put("ACCESS_TOKEN", access_token);
		saveTokenParam.put("REFRESH_TOKEN", refreshToken);
		saveTokenParam.put("EXPIRES_IN", expiresIn);
		if(reTokenExpiresIn != null) {
			saveTokenParam.put("RE_EXPIRES_IN", reTokenExpiresIn);	
		}
		saveTokenParam.put("SOCIAL_ID", socialId);
		saveTokenParam.put("CIP", ComUtil.getAddress(request));
		comDao.insert("oauth.I_COMM_OAUTH_TOKEN", saveTokenParam);
		
		oauthParam.put("SOCIAL_ID", socialId);
		
		List<Map> loginList = comDao.selectList("oauth.S_COMM_OAUTH", oauthParam);
		
		Map<String,String> loginResult = null;
		if(loginList != null) {
			//연결된 계정이 1개인경우
			if(loginList.size() == 1) {
				loginResult = loginList.get(0);
			}
			//대표계정 설정없이 2개이상 인경우 가장 최근에 로그인한 계정으로 로그인한다. 
			else if(loginList.size() > 1) {
				String ids = "";
				for (int i = 0; i < loginList.size(); i++) {
					if(i == 0) {
						ids += loginList.get(i).get("COMM_USER_ID");	
					}
					else {
						ids += "," + loginList.get(i).get("COMM_USER_ID");
					}
					
				}
				
				Map lastUserParam = new HashMap();
				lastUserParam.put("IDS", ids);
				Map lastUser = comDao.selectOne("oauth.S_COMM_LOGIN_LAST", lastUserParam);
				Optional one = loginList.stream().filter(x->  String.valueOf(lastUser.get("COMM_USER_ID")).equals(String.valueOf(x.get("COMM_USER_ID")))).findFirst();
				loginResult = (Map<String, String>) one.get();
			}
		}
		
		
		HttpSession session = request.getSession();
		
		session.setAttribute("OAUTH_TYPE", p_type);
		
		//연동된 아이디가 없음
		if(loginResult == null) {
			
			session.setAttribute("SOCIAL_ID", socialId);
			return "/oauth/regist";
		}
		else {
			comService.setSession(request, loginResult);
		}
		
		return "/";
	}
	
	//토큰받기
	public Map getToken(String p_type, HttpServletRequest request) {
		
		Map httpParam = new HashMap();
		Map headerParam = new HashMap();
		Map bodyParam = new HashMap();
		
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		
		headerParam.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		bodyParam.put("grant_type", "authorization_code");
		httpParam.put("method", RequestMethod.POST);
		
		String url = null;
		
		if(request.getLocalPort() == 80 || request.getLocalPort() == 443 ) {
			url = request.getScheme() + "://" + request.getServerName();
		}
		else {
			url = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
		}
		
		bodyParam.put("code", code);
		bodyParam.put("state", state);
		
		switch (p_type) {
		case "KAKAO":
			httpParam.put("url", "https://kauth.kakao.com/oauth/token");
			bodyParam.put("client_id", InitBean.getKAKAO_REST_API());
			bodyParam.put("redirect_uri", url + KAKAO_REDIRECT_URI);
			break;
			
		case "NAVER":
			httpParam.put("url", "https://nid.naver.com/oauth2.0/token");
			bodyParam.put("client_id", InitBean.getNAVER_CLIENT_ID());
			bodyParam.put("client_secret", InitBean.getNAVER_CLIENT_SECRET());
			bodyParam.put("redirect_uri", url + NAVER_REDIRECT_URI);			
			break;
			
		default:
			break;
		}
		
		Map tokenResult = HttpUtil.call(httpParam, headerParam, bodyParam);
		
		//토큰받기 오류발생
		if(!"200".equals(String.valueOf( tokenResult.get("responseCode") ))) {
			logger.info("responseCode ::: " + String.valueOf( tokenResult.get("responseCode") ));
			return null;
		}
				
		return (Map)tokenResult.get("data");
	}
	
	//사용자 조회
	public Map getUserInfo(String p_type, String p_access_token) {
		Map httpParam = new HashMap();
		Map headerParam = new HashMap();
		
		switch (p_type) {
		case "KAKAO":
			httpParam.put("url", "https://kapi.kakao.com/v2/user/me");
			break;
			
		case "NAVER":
			httpParam.put("url", "https://openapi.naver.com/v1/nid/me");		
			break;
			
		default:
			break;
		}
		
		
		httpParam.put("method", RequestMethod.GET);
		headerParam.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		headerParam.put("Authorization", "Bearer " + p_access_token );
		
		Map userResult = HttpUtil.call(httpParam, headerParam, null);
		//사용자 조회 오류발생
		if(!"200".equals(String.valueOf( userResult.get("responseCode") ))) {
			logger.info("responseCode ::: " + String.valueOf( userResult.get("responseCode") ));
			return null;
		}
		return (Map)userResult.get("data");
	}
		
	//로그아웃
	public void logout(String p_type, String p_access_token) {
		Map httpParam = new HashMap();
		Map headerParam = new HashMap();
		Map bodyParam = new HashMap();
		
		switch (p_type) {
		case "NAVER":
			httpParam.put("url", "https://nid.naver.com/oauth2.0/token");	
			bodyParam .put("client_id", InitBean.getNAVER_CLIENT_ID() );
			bodyParam .put("client_secret", InitBean.getNAVER_CLIENT_SECRET() );
			bodyParam .put("access_token", p_access_token );
			bodyParam .put("grant_type", "delete" );
			break;
			
		default:
			break;
		}
				
		httpParam.put("method", RequestMethod.GET);
		headerParam.put("Content-Type", "application/x-www-form-urlencoded" );
		
		HttpUtil.call(httpParam, headerParam, bodyParam);
	}

}
