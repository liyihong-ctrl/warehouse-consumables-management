package frame.server.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64ConvertUtil {

	public static final String FORMATE = "utf-8";

	public static String encode(String str) throws UnsupportedEncodingException {
		byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes(FORMATE));
		return new String(encodeBytes);
	}

	public static String decode(String str) throws UnsupportedEncodingException {
		byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes(FORMATE));
		return new String(decodeBytes);
	}

}
