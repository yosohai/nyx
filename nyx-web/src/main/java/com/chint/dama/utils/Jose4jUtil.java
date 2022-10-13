package com.chint.dama.utils;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class Jose4jUtil {
    // 生成一个RSA密钥对，用于签署和验证JWT，包装在JWK中
    public static RsaJsonWebKey rsaJsonWebKey = RsaJsonWebKeyUtil.singletonHolder();

    public static String generateToken() throws JoseException, MalformedClaimException {
        // 创建claims，这将是JWT的内容 B部分
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("api"); // 谁创建了令牌并签署了它
        claims.setAudience("web"); // 令牌将被发送给谁
        claims.setExpirationTimeMinutesInTheFuture(100); // 令牌失效的时间长（从现在开始10分钟）
        claims.setGeneratedJwtId(); // 令牌的唯一标识符
        claims.setIssuedAtToNow(); // 当令牌被发布/创建时（现在）
        claims.setNotBeforeMinutesInThePast(2); // 在此之前，令牌无效（2分钟前）
        claims.setSubject("subject"); // 主题 ,是令牌的对象
        claims.setClaim("email", "help@chint.com"); // 可以添加关于主题的附加 声明/属性
        List groups = CollectionUtils.arrayToList(new String[]{"group-one", "other-group", "group-three"});
        claims.setStringListClaim("groups", groups); // 多个属性/声明 也会起作用，最终会成为一个JSON数组

        // JWT是一个JWS和/或一个带有JSON声明的JWE作为有效负载。
        // 在这个例子中，它是一个JWS，所以我们创建一个JsonWebSignature对象。
        JsonWebSignature jws = new JsonWebSignature();

        // JWS的有效负载是JWT声明的JSON内容
        jws.setPayload(claims.toJson());

        // JWT使用私钥签署
        jws.setKey(rsaJsonWebKey.getPrivateKey());

        /*
         * 设置关键ID（kid）头，因为这是一种礼貌的做法。 在这个例子中，我们只有一个键但是使用键ID可以帮助 促进平稳的关键滚动过程
         */
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // 在jw/jws上设置签名算法，该算法将完整性保护声明
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        /*
         * 签署JWS并生成紧凑的序列化或完整的jw/JWS 表示，它是由三个点（'.'）分隔的字符串
         * 在表单头.payload.签名中使用base64url编码的部件 如果你想对它进行加密，你可以简单地将这个jwt设置为有效负载
         * 在JsonWebEncryption对象中，并将cty（内容类型）头设置为“jwt”。
         */
        return jws.getCompactSerialization();
    }

    public static boolean checkJwt(String jwt) throws MalformedClaimException {
        /*
         * 使用JwtConsumer builder构建适当的JwtConsumer，它将 用于验证和处理JWT。 JWT的具体验证需求是上下文相关的， 然而,
         * 通常建议需要一个（合理的）过期时间，一个受信任的时间 发行人, 以及将你的系统定义为预期接收者的受众。
         * 如果JWT也被加密，您只需要提供一个解密密钥对构建器进行解密密钥解析器。
         */
        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime()  // JWT必须有一个有效期时间
                .setAllowedClockSkewInSeconds(30) // 允许在验证基于时间的令牌时留有一定的余地，以计算时钟偏差。单位/秒
                .setRequireSubject() // 主题声明
                .setExpectedIssuer("api") // JWT需要由谁来发布,用来验证 发布人
                .setExpectedAudience("web") // JWT的目的是给谁, 用来验证观众
                .setVerificationKey(rsaJsonWebKey.getKey()) // 用公钥验证签名 ,验证秘钥
                .setJwsAlgorithmConstraints( // 只允许在给定上下文中预期的签名算法,使用指定的算法验证
                        new AlgorithmConstraints(org.jose4j.jwa.AlgorithmConstraints.ConstraintType.PERMIT, // 白名单
                                AlgorithmIdentifiers.RSA_USING_SHA256))
                .build(); // 创建JwtConsumer实例
        boolean result = true;
        try {
            // 验证JWT并将其处理为jwtClaims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
//			如果JWT失败的处理或验证，将会抛出InvalidJwtException。
        } catch (InvalidJwtException e) {
            result = false;
            if (e.hasExpired()) {
                System.out.println("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
            }
            if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
                System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
            }
        }
        return result;
    }

}
