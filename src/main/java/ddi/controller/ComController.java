package ddi.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ddi.init.InitBean;
import ddi.service.ComService;
import ddi.util.ComUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ComController {
	
	//간편로그인
	private final static String API_STATE_CODE = ComUtil.getRandomKey(10);
	
	@Value("#{commonConfig['KAKAO_REDIRECT_URI']}")
	private String KAKAO_REDIRECT_URI;
	@Value("#{commonConfig['NAVER_REDIRECT_URI']}")
	private String NAVER_REDIRECT_URI;
		
	@Value("#{commonConfig['webModulePath']}")
	private String webModulePath;
	
	@Autowired
	ComService comService;
		
	private static final Logger logger = LoggerFactory.getLogger(ComController.class);
		
	/**
	 * 메인화면 호출
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request, Model m) {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
				
		m.addAttribute("KAKAO_REST_API", InitBean.getKAKAO_REST_API());
		m.addAttribute("KAKAO_REDIRECT_URI", KAKAO_REDIRECT_URI);
		
		m.addAttribute("NAVER_CLIENT_ID", InitBean.getNAVER_CLIENT_ID());
		m.addAttribute("NAVER_REDIRECT_URI", NAVER_REDIRECT_URI);
		
		m.addAttribute("OAUTH_TYPE", request.getSession().getAttribute("OAUTH_TYPE"));
		
		return "index";
	}
	
	/**
	 * 로그인 페이지이동
	 */
	@RequestMapping(value = "/loginPage", method = RequestMethod.GET)
	public String loginPage(HttpServletRequest request, Model m) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");		
		ComUtil.getKeyPair(request);
		
		m.addAttribute("API_STATE_CODE", API_STATE_CODE);
		
		m.addAttribute("KAKAO_REST_API", InitBean.getKAKAO_REST_API());
		m.addAttribute("KAKAO_REDIRECT_URI", KAKAO_REDIRECT_URI);
		
		m.addAttribute("NAVER_CLIENT_ID", InitBean.getNAVER_CLIENT_ID());
		m.addAttribute("NAVER_REDIRECT_URI", NAVER_REDIRECT_URI);
		
		return "loginPage";
	}
	
	/**
	 * 회원가입 페이지이동
	 */
	@RequestMapping(value = "/registPage", method = RequestMethod.GET)
	public String registPage(HttpServletRequest request, Model m) {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		
		return "registPage";
	}
	/**
	 * 회원가입 처리
	 */
	@RequestMapping(value = "/registAction", method = RequestMethod.POST)
	public @ResponseBody Map registAction(HttpServletRequest request) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		Map<String, String> resultMap = new HashMap();
		
		resultMap.put("result", comService.registAction(request));
		
		return resultMap;
	}
	/**
	 * 로그인처리
	 */
	@RequestMapping(value = "/loginAction", method = RequestMethod.POST)
	public @ResponseBody Map loginAction(HttpServletRequest request) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		Map<String, String> resultMap = new HashMap();
		
		resultMap.put("result", comService.loginAction(request, null));
		
		return resultMap;
	}
	/**
	 * 로그아웃Action
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		
		HttpSession session = request.getSession();
		
		session.invalidate();
		
		return "redirect:/loginPage";
	}
	/**
	 * 화면변경
	 */
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public String page(HttpServletRequest request, Model m) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		
		comService.page(request, m);
		return "index";
	}
	
	/**
	 * ajax
	 */
	@RequestMapping(value = "/ajax", method = RequestMethod.POST)
	public @ResponseBody Map ajax(HttpServletRequest request) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		
		return comService.ajax(request);
	}
}
