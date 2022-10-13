package com.chint.dama.utils;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;

/**
 * @Author: lzqing
 * @Description: Rsa秘钥，内部类单例实现
 * @Date: Created in 2021/6/6
 */
public class RsaJsonWebKeyUtil {
    public static RsaJsonWebKey rsaJsonWebKey = null;

    private RsaJsonWebKeyUtil() {
        try {
            rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
            rsaJsonWebKey.setKeyId("saas-jwt");
        } catch (JoseException e) {
            e.printStackTrace();
        }
    }

    public static RsaJsonWebKey singletonHolder() {
        RsaJsonWebKeyUtil instance = InnerClass.INSTANCE;
        return rsaJsonWebKey;
    }

    private static class InnerClass {
        private static final RsaJsonWebKeyUtil INSTANCE = new RsaJsonWebKeyUtil();
    }
}
