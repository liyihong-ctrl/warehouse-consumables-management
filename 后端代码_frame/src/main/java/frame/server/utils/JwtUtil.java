package frame.server.utils;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import frame.server.constant.JwtClaimConstant;

public class JwtUtil {

	/**
	 * token的有效期限
	 */
	private static long EXPIRE_TIME = 6 * 60 * 60 * 1000;

	/**
	 * 生成token
	 */
	public static String sign(String account, String password) {
		// 过期时间
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);

		// 采用用户密码对应的MD5加密密文生成算法标记
		Algorithm algorithm = Algorithm.HMAC256(password);

		return JWT.create()
				// 附带账户信息
				.withClaim(JwtClaimConstant.ACCOUNT, account)
				// 设置过期时间
				.withExpiresAt(date)
				// 创建一个新的jwt，并用给定算法进行标记
				.sign(algorithm);
	}

	/**
	 * 验证token是否正确
	 * @param token
	 * @param account
	 * @param secret 用户密码对应的MD5密文
	 * @return
	 */
	public static boolean verify(String token, String account, String secret) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm)
					// token中附带了account信息
					.withClaim(JwtClaimConstant.ACCOUNT, account)
					.build();
			verifier.verify(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getClaim(String token, String claim) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim(claim).asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 验证token是否即将过期
	 * 过期危险期:距离过期60分钟内
	 * @param token
	 * @return
	 */
	public static boolean isAfterExpiration(String token) {
		// 判断是否已经过期
		if(isExpiration(token)) {
			return false;
		}
		
		long expireDate = JWT.decode(token).getExpiresAt().getTime();
		long now = new Date().getTime();
		
		if(now == expireDate) {
			return false;
		}
		
		return ((expireDate - now) / 1000 / 60) < 60;
	}

	/**
	 * 验证token是否过期
	 * @param token
	 * @return
	 */
	public static boolean isExpiration(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return jwt.getExpiresAt().before(new Date());
	}

}
