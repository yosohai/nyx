package com.chint.dama.dao;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

public class CommonFields {
    /**
     * 主键(ASSIGN_ID雪花算法)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "createtime", fill = FieldFill.INSERT)
    private String createtime;

    /**
     * 修改时间
     */
    @TableField(value = "updatetime", fill = FieldFill.INSERT_UPDATE)
    private String updatetime;

    /**
     * 创建人
     */
    private String createman;
    /**
     * 修改人
     */
    private String updateman;

    /**
     * 逻辑删除字段
     */
    @TableField(value = "deleteflag", fill = FieldFill.INSERT)
    @TableLogic
    private Boolean deleteflag;


}
