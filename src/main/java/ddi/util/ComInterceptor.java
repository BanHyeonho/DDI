package ddi.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ddi.init.InitBean;

public class ComInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		String requestURI = request.getRequestURI();
		String loginSessionYn = String.valueOf(request.getSession().getAttribute("LOGIN_SESSION_YN"));
		
//		//로그인 상태가 아니면서 특정 url 접근시 허용하지 않는다.
		//비로그인 상태
		if( !"1".equals(loginSessionYn) ) {
			
			if(requestURI.equals("/registPage")
			|| requestURI.equals("/registAction")
			|| requestURI.equals("/loginPage")
			|| requestURI.equals("/loginAction")
			|| requestURI.equals("/oauth/regist")
			|| requestURI.equals("/oauth/registAction")
			) {
				return true;
			}
			else if(requestURI.equals("/")) {
				response.sendRedirect("/loginPage");
			}
			else {
				response.sendError(9999);
			}
			
			return false;
		}
		//로그인한 상태
		else {
			
			if(requestURI.equals("/registPage")
			|| requestURI.equals("/registAction")
			|| requestURI.equals("/loginPage")
			|| requestURI.equals("/loginAction")
			|| requestURI.equals("/oauth/regist")
			|| requestURI.equals("/oauth/registAction")
			) {
				response.sendRedirect("/");
				return false;
			}
			
		}
		
		return true;
	}

}
