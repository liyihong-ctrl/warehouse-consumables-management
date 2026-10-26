package frame.server.utils;

import java.util.regex.Pattern;

/**
 * 校验工具包
 * 
 * @author Administrator
 *
 */
public class ValidatorUtil {

	public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,20}$";

	public static final String REGEX_PASSWORD = "^[A-Za-z0-9_]{6,20}$";

	public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

	public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

	public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

	public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	public static final String WAREHOUSE_CODE = "^[A-Za-z0-9]{1,20}$";

	public static final String CONSUMABLE_CODE = "^[A-Za-z0-9]{1,20}$";

	/**
	 * 用户名校验
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isUsername(String username) {
		return Pattern.matches(REGEX_USERNAME, username);
	}

	/**
	 * 密码校验 数字+字母+下划线
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isPassword(String password) {
		return Pattern.matches(REGEX_PASSWORD, password);
	}

	/**
	 * 手机号校验
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	/**
	 * 邮箱校验
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		return Pattern.matches(REGEX_EMAIL, email);
	}

	/**
	 * 中文校验
	 * 
	 * @param chinese
	 * @return
	 */
	public static boolean isChinese(String chinese) {
		return Pattern.matches(REGEX_CHINESE, chinese);
	}

	/**
	 * IdCard校验
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIDCard(String idCard) {
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}

	/**
	 * url校验
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url) {
		return Pattern.matches(REGEX_URL, url);
	}

	/**
	 * IP地址校验
	 * 
	 * @param ipAddr
	 * @return
	 */
	public static boolean isIPAddr(String ipAddr) {
		return Pattern.matches(REGEX_IP_ADDR, ipAddr);
	}

	/**
	 * 项目业务相关：仓库编号校验
	 * 
	 * 数字+字母
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isWarehouseCode(String code) {
		return Pattern.matches(WAREHOUSE_CODE, code);
	}

	/**
	 * 项目业务相关：消耗品Code校验
	 * 
	 * 数字+字母
	 * 
	 * @param code
	 * @return
	 */
	public static boolean isConsumableCode(String code) {
		return Pattern.matches(CONSUMABLE_CODE, code);
	}

}
