package com.chint.dama.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lzqing
 * @description 访问者表
 * @date 2021-11-12
 */
@Data
@Accessors(chain = true)
public class VisitorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * salt
     */
    private String salt;

    /**
     * 访问者类型
     */
    private String visitortype;

    /**
     * description
     */
    private String description;

    public VisitorVO() {
    }
}