package com.chint.dama.base.mapstruct.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonDTO {
    /**
     * 主键(ASSIGN_ID雪花算法)
     */
    private Long id;

    /**
     * 创建时间
     */
    private String createtime;

    /**
     * 修改时间
     */
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
    private Boolean deleteflag;

}
