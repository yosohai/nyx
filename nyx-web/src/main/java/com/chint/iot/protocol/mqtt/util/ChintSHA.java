package com.chint.iot.protocol.mqtt.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: Lusay
 * Date  : 2018/8/7
 */
public class ChintSHA
{
    private static final String HMACSHA256_KEY = "zpsUQIxeg5vbr9N0";

    public static String HMACSHA256(String data) throws Exception
    {
        return HMACSHA256(data, HMACSHA256_KEY);
    }

    public static String HMACSHA256(String data, String key)
    {
          try
          {
             byte[] dataBytes = data.getBytes();
             byte[] keys = key.getBytes();
             SecretKeySpec signingKey = new SecretKeySpec(keys, "HmacSHA256");
             Mac mac = Mac.getInstance("HmacSHA256");
             mac.init(signingKey);

             return byte2hex(mac.doFinal(dataBytes));
          }
          catch (NoSuchAlgorithmException e)
          {
             e.printStackTrace();
          }
          catch (InvalidKeyException e)
          {
            e.printStackTrace();
          }

          return null;
    }

    public static String SHA256(String data)
    {
        MessageDigest messageDigest;
        String encodeStr = "";

        try
        {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
            encodeStr = byte2hex(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return encodeStr;
    }

    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++)
        {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
            {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }
}
