package com.chint.dama.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *属性工具类
 */
public class PropertiesUtil {

    private final Properties pps;

    public PropertiesUtil(String filePath) throws IOException {
        pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
    }

    public String read(String key) {
        return pps.getProperty(key);
    }
}
