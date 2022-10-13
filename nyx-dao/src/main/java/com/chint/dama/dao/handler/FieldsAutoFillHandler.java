package com.chint.dama.dao.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 新增或修改时给部分字段赋默认值
 */
@Slf4j
@Component
public class FieldsAutoFillHandler implements MetaObjectHandler {
    /**
     * 插入时候的处理策略
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("DateTimeAutoFillHandler insertFill starte ...");
//        this.setFieldValByName("gmtCreate", new Date(), metaObject);
//        this.setFieldValByName("gmtModified", new Date(), metaObject);
        this.setFieldValByName("deleteflag", false, metaObject);
//        this.strictInsertFill(metaObject, "createtime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
//        this.strictInsertFill(metaObject, "updatetime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "createtime", () -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()), String.class);
        this.strictInsertFill(metaObject, "updatetime", () -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()), String.class);

    }

    /**
     * 更新时候的处理策略
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("DateTimeAutoFillHandler updateFill starte ...");
        this.setFieldValByName("updatetime", new Date(), metaObject);
    }
}
