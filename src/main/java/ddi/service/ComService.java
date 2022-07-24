package ddi.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import ddi.dao.ComDao;
import ddi.util.ComUtil;

@Service("ComService")
public class ComService {

	//간편로그인
	private final String API_STATE_CODE = ComUtil.getRandomKey(10);
		
	@Value("#{commonConfig['NAVER_REDIRECT_URI']}")
	private String NAVER_REDIRECT_URI;
	@Value("#{commonConfig['NAVER_REDIRECT_URI_LINK']}")
	private String NAVER_REDIRECT_URI_LINK;
	
	@Autowired
	ComDao comDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ComService.class);
	
	
	/**
	 * 화면전환
	 * @param request
	 * @param m
	 * @throws Exception 
	 */
	public void page(HttpServletRequest request, Model m) throws Exception {
		String pageName = request.getParameter("PAGE_NAME");
		m.addAttribute("PAGE_NAME", pageName);
		m.addAttribute("jsLink", "/resources/viewJs/contents/" + pageName + ".js");
		
		
		if("news".equals(pageName)) {
			Map param = new HashMap();
			param.put("COMM_USER_ID", String.valueOf(request.getSession().getAttribute("COMM_USER_ID")));
			m.addAttribute("NEWS", comDao.selectList("api.S_API_NEWS", param));	
		}
		
	}
	
	/**
	 * ajax 처리
	 */
	public Map ajax(HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap();
		
		String queryId = request.getParameter("QUERY_ID");
		String param = request.getParameter("PARAM");
		Map paramMap = new JSONObject(param).toMap();
		
		String userId = String.valueOf(request.getSession().getAttribute("COMM_USER_ID"));
		String ip = ComUtil.getAddress(request);
				
		paramMap.put("COMM_USER_ID", userId);
		paramMap.put("CID", userId);
		paramMap.put("CIP", ip);
		paramMap.put("MID", userId);
		paramMap.put("MIP", ip);
		
		if(queryId.indexOf(".S_") > -1) {
			result.put("data", comDao.selectList(queryId, paramMap));
		}
		else {
			comDao.selectOne(queryId, paramMap);
		}
		
		result.put("state", "success");
		return result;
	}
		
	/**
	 * 회원가입처리
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public String registAction(HttpServletRequest request) throws Exception{
		
		Map<String, String> paramMap = ComUtil.getParameterMap(request);
		
		String result;
		String originPwd;
		String pwd;
		
		originPwd = paramMap.get("PWD");
		pwd = ComUtil.decrypt(request.getSession().getAttribute("privateKey").toString(), originPwd);

		String salt = ComUtil.getSalt();
		String encrytPwd = ComUtil.getSHA512( pwd , salt);
		String ip = ComUtil.getAddress(request);
		
		paramMap.put("SALT", salt);
		paramMap.put("PWD", encrytPwd);
		
		paramMap.put("CIP", ip);
		paramMap.put("MIP", ip);
		try {
			//사용자테이블
			comDao.insert("com.I_COMM_USER", paramMap);
			//패스워드SALT
			comDao.insert("com.I_SALT", paramMap);
			
			paramMap.put("PWD", originPwd);
			result = loginAction(request, paramMap);
		}
		catch (DuplicateKeyException e) {
			result = "duplicatedId";
		}
		
		return result;
	}
	
	/**
	 * 로그인처리
	 * @param request
	 * @param param : 회원가입후 가입한정보로 바로 로그인처리
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public String loginAction(HttpServletRequest request, Map param) throws Exception{
		
		Map<String, String> paramMap = (param == null ? ComUtil.getParameterMap(request) : param);
		
		String result;
		
		Map<String,String> loginResult = passwordChk(request.getSession().getAttribute("privateKey").toString(), paramMap);
				
		if(loginResult == null) {
			result = "chkIdPwd";
		}
		else {
			setSession(request, loginResult);
			result = "success";
		}
		return result;
	}
	
	/**
	 * 비밀번호 체크
	 * @param p_privateKey
	 * @param p_param
	 * @return
	 * @throws Exception 
	 */
	public Map passwordChk(String p_privateKey, Map<String, String> p_param) throws Exception {
		Map<String, String> result = null;
		
		String pwd = ComUtil.decrypt(p_privateKey, p_param.get("PWD"));
		
		Map<String,String> salt = comDao.selectOne("com.S_SALT", p_param);
		
		String shaPwd = ComUtil.getSHA512( pwd , salt == null ? "" : salt.get("SALT") );
		
		p_param.put("PWD", shaPwd);
		result = comDao.selectOne("com.S_LOGIN", p_param);
		
		return result;
	}
	
	/**
	 * 로그인후 세션값 저장
	 * @param session
	 * @param p_param(COMM_USER_ID, LOGIN_ID, USER_NAME, CDT)
	 */
	public void setSession(HttpServletRequest request, Map p_param) {
		HttpSession session = request.getSession();
		
		session.setAttribute("LOGIN_SESSION_YN", "1");
		session.setAttribute("COMM_USER_ID", p_param.get("COMM_USER_ID"));
		session.setAttribute("LOGIN_ID", p_param.get("LOGIN_ID"));
		session.setAttribute("USER_NAME", p_param.get("USER_NAME"));
					
		try {
			//로그인 이력 저장
			Map logParam = new HashMap();
			logParam.put("LOGIN_ID", session.getAttribute("LOGIN_ID"));
			logParam.put("COMM_USER_ID", session.getAttribute("COMM_USER_ID"));
			logParam.put("CIP", ComUtil.getAddress(request) );
			comDao.insert("com.I_COMM_LOGIN_LOG", logParam);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
