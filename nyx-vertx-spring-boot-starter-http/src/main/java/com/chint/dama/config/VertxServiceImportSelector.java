package com.chint.dama.config;

import com.chint.dama.annotation.EnableVertx;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 收集需要导入的配置类
 */
public class VertxServiceImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(
                        EnableVertx.class.getName()));
        //在这里可以拿到所有注解的信息，可以根据不同注解的和注解的属性来返回不同的class,
        // 从而达到开启不同功能的目的
        return new String[]{VertxService.class.getName()};
    }
}