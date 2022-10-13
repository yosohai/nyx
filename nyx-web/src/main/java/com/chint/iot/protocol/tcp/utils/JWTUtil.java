package com.chint.iot.protocol.tcp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-04-06 14:15
 */
@Slf4j
public class JWTUtil {

	/**
	 * 加密生成token
	 *
	 * @param maxAge 有效时长
	 * @param secret 服务器私钥
	 * @param <T>
	 * @return
	 */
	public static <T> String createToken(long maxAge, String secret) {
		try {
			final Algorithm signer = Algorithm.HMAC256(secret);//生成签名
			String token = JWT.create()
					.withIssuer("chint")
					.withSubject("chint-http")
					.withIssuedAt(new Date(System.currentTimeMillis()))
					.withExpiresAt(new Date(System.currentTimeMillis() + maxAge))
					.sign(signer);
			return token;
		} catch (Exception e) {
			log.error("生成token异常：", e);
			return null;
		}
	}

	/**
	 * 解析验证token
	 *
	 * @param token  加密后的token字符串
	 * @param secret 服务器私钥
	 * @return
	 */
	public static Boolean verifyToken(String token, String secret) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT jwt = verifier.verify(token);
			long expireTime = jwt.getExpiresAt().getTime();
			long currentTime = System.currentTimeMillis();
			if (currentTime > expireTime) {
				log.info("token已经过期expireTime={},currentTime={}", expireTime, currentTime);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("校验token失败：", e);
		}
		return false;
	}

	public static void main(String[] args) {
		String secret ="9999999999999999999";
		String token = JWTUtil.createToken(60*60*1000,secret);
		System.out.println(token);
		System.out.println(verifyToken(token,secret));
		System.out.println(verifyToken(token,"8888888888888888"));
	}
}
