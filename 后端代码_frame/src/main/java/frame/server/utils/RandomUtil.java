package frame.server.utils;

import java.util.UUID;

public class RandomUtil {
	
	/**
	 * 生成随机ID
	 * 
	 * @return
	 */
	public static String genarateId() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	
	/**
	 * 生成随便ID(带前缀)
	 * 
	 * @param prefix
	 * @return
	 */
	public static String genarateId(String prefix) {
		String uuid = prefix + UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	
}
