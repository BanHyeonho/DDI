package ddi.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ddi.service.ApiService;

/**
 * API
 * @author Administrator
 *
 */
@RequestMapping("/api")
@Controller("ApiController")
public class  ApiController{

	@Autowired
	ApiService api;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
		
	/**
	 * 뉴스데이터 가져오기
	 */
	@RequestMapping(value = "/news", method = RequestMethod.POST)
	public @ResponseBody Map news(HttpServletRequest request) throws Exception {
		logger.info("URL is {}.", "[" + request.getRequestURI() + "]");
		api.news(request);
		return null;
	}
}
