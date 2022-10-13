package com.chint.dama.utils;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.List;
import java.util.stream.Stream;

/**
 * ReflectionUtil
 */
public final class ReflectionUtil {

    public static Reflections getReflections(String... packageAddress) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        FilterBuilder filterBuilder = new FilterBuilder();
        Stream.of(packageAddress).forEach(str -> configurationBuilder.addUrls(ClasspathHelper.forPackage(str.trim())));
        filterBuilder.includePackage(packageAddress);
        configurationBuilder.filterInputsBy(filterBuilder);
        return new Reflections(configurationBuilder);
    }

    public static Reflections getReflections(List<String> packageAddresses) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        FilterBuilder filterBuilder = new FilterBuilder();
        packageAddresses.forEach(str -> {
            configurationBuilder.addUrls(ClasspathHelper.forPackage(str.trim()));
            filterBuilder.includePackage(str.trim());
        });
        configurationBuilder.filterInputsBy(filterBuilder);
        return new Reflections(configurationBuilder);
    }

}
