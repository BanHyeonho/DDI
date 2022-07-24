package ddi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class tldUtil {
	/**
	 * 스크립트,CSS 캐시방지
	 * @return
	 */
	public static String jsNow() {
		SimpleDateFormat jsFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return jsFormat.format(new Date());
	}
}
