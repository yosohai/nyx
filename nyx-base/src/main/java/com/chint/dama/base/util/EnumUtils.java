package com.chint.dama.base.util;

import cn.hutool.core.util.ClassUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnumUtils {

    private static final List<Map<String, String>> propsList = new ArrayList<>();

    public static List<Map<String, String>> getPropsList() {
        return propsList;
    }

    static {
        String[] split = {"com.chint.dama.enums"};
        Set<Class<?>> classes = new HashSet<>();
        Arrays.stream(split).forEach(str -> {
            final Set<Class<?>> clazz = ClassUtil.scanPackage(str);
            classes.addAll(clazz);
        });

        classes.stream().forEach(e -> {
            Map<String, String> props = new HashMap<>();
            String simpleName = e.getSimpleName();
            String lowerCaseName = toLowerCaseFirstOne(simpleName);
            if (simpleName.contains("Enum")) {
                lowerCaseName = lowerCaseName.replace("Enum", "");
                props.put("suffix", "Enum");
            } else {
                props.put("suffix", "");
            }
            props.put(lowerCaseName, simpleName);
            propsList.add(props);
        });
    }

    /**
     * java枚举类转List<Map<String, Object>>集合
     *
     * @param clazz
     * @return null-该class不是枚举类型  []-该枚举类型没有自定义字段  list-获取该枚举类型的List<Map<String, Object>>返回结果
     */
    public static List<Map<String, Object>> enumToListMap(Class<?> clazz) {
        List<Map<String, Object>> resultList = null;
        // 判断是否是枚举类型
        if ("java.lang.Enum".equals(clazz.getSuperclass().getCanonicalName())) {
            resultList = new ArrayList<>();
            // 获取所有public方法
            Method[] methods = clazz.getMethods();
            List<Field> fieldList = new ArrayList<>();
            for (int i = 0; i < methods.length; i++) {
                String methodName = methods[i].getName();
                if (methodName.startsWith("get") && !"getDeclaringClass".equals(methodName)
                        && !"getClass".equals(methodName)) { // 找到枚举类中的以get开头的(并且不是父类已定义的方法)所有方法
                    Field field = null;
                    try {
                        field = clazz.getDeclaredField(StringUtils.uncapitalize(methodName.substring(3))); // 通过方法名获取自定义字段
                    } catch (NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                    }
                    if (field != null) { // 如果不为空则添加到fieldList集合中
                        fieldList.add(field);
                    }
                }
            }
            if (!fieldList.isEmpty()) { // 判断fieldList集合是否为空
                Map<String, Object> map = null;
                Enum<?>[] enums = (Enum[]) clazz.getEnumConstants(); // 获取所有枚举
                for (int i = 0; i < enums.length; i++) {
                    map = new HashMap<>();
                    for (int l = 0, len = fieldList.size(); l < len; l++) {
                        Field field = fieldList.get(l);
                        field.setAccessible(true);
                        try {
                            map.put(field.getName(), field.get(enums[i])); // 向map集合添加字段名称 和 字段值
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    resultList.add(map);// 将Map添加到集合中
                }
            }
        }
        return resultList;
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}
