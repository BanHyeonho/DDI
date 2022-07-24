package ddi.init;

import javax.annotation.PostConstruct;

import org.jasypt.spring31.properties.EncryptablePropertiesPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitBean{

	@Autowired
	private EncryptablePropertiesPropertySource commonConfig;
	
	private static String KAKAO_JAVASCRIPT;
	private static String KAKAO_REST_API;
	private static String KAKAO_ADMIN;
	
	private static String NAVER_CLIENT_ID;
	private static String NAVER_CLIENT_SECRET;
		
	/**
	 * 의존성 주입후 초기화
	 * @throws Exception
	 */
	@PostConstruct
    private void init() throws Exception {
				
		KAKAO_JAVASCRIPT = (String) commonConfig.getProperty("KAKAO_JAVASCRIPT");
		KAKAO_REST_API = (String) commonConfig.getProperty("KAKAO_REST_API");
		KAKAO_ADMIN = (String) commonConfig.getProperty("KAKAO_ADMIN");
		NAVER_CLIENT_ID = (String) commonConfig.getProperty("NAVER_CLIENT_ID");
		NAVER_CLIENT_SECRET = (String) commonConfig.getProperty("NAVER_CLIENT_SECRET");
		
    }

	public static String getKAKAO_JAVASCRIPT() {
		return KAKAO_JAVASCRIPT;
	}

	public static String getKAKAO_REST_API() {
		return KAKAO_REST_API;
	}

	public static String getKAKAO_ADMIN() {
		return KAKAO_ADMIN;
	}

	public static String getNAVER_CLIENT_ID() {
		return NAVER_CLIENT_ID;
	}

	public static String getNAVER_CLIENT_SECRET() {
		return NAVER_CLIENT_SECRET;
	}

}