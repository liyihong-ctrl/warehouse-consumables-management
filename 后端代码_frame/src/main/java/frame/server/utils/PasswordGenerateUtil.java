package frame.server.utils;

import java.util.Random;

/**
 * 密码生成器 说明：自动生成6位复杂密码
 * 
 * @author Administrator
 *
 */
public class PasswordGenerateUtil {

	// 特殊字符串
	private static final String SPECIAL_CHARS = "_";

	// 长度
	private static final int PASSWORD_LENGTH = 6;

	/**
	 * 随机密码生成器
	 * 
	 * @return
	 */
	public static String randomPassword() {
		char[] chars = new char[PASSWORD_LENGTH];
		Random rnd = new Random();
		chars[nextIndex(chars, rnd)] = nextSpecialChar(rnd);
		chars[nextIndex(chars, rnd)] = nextUpperlLetter(rnd);
		chars[nextIndex(chars, rnd)] = nextLowerLetter(rnd);
		chars[nextIndex(chars, rnd)] = nextNumLetter(rnd);
		for (int i = 0; i < PASSWORD_LENGTH; i++) {
			if (chars[i] == 0) {
				chars[i] = nextChar(rnd);
			}
		}
		return new String(chars);
	}

	private static int nextIndex(char[] chars, Random rnd) {
		int index = rnd.nextInt(chars.length);
		while (chars[index] != 0) {
			index = rnd.nextInt(chars.length);
		}
		return index;
	}

	private static char nextSpecialChar(Random rnd) {
		return SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length()));
	}

	private static char nextUpperlLetter(Random rnd) {
		return (char) ('A' + rnd.nextInt(26));
	}

	private static char nextLowerLetter(Random rnd) {
		return (char) ('a' + rnd.nextInt(26));
	}

	private static char nextNumLetter(Random rnd) {
		return (char) ('0' + rnd.nextInt(10));
	}

	private static char nextChar(Random rnd) {
		switch (rnd.nextInt(4)) {
		case 0:
			return (char) ('a' + rnd.nextInt(26));
		case 1:
			return (char) ('A' + rnd.nextInt(26));
		case 2:
			return (char) ('0' + rnd.nextInt(10));
		default:
			return SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length()));
		}
	}

	public static void main(String[] args) {
		System.out.println(randomPassword());
	}
}
