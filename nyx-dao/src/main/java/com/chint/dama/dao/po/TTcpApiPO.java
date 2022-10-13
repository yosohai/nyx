package com.chint.dama.dao.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chint.dama.dao.CommonFields;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by Ruohong Cheng on 2021/12/1 13:16
 */
@Data
@Accessors(chain = true)
@TableName("t_tcp_api")
public class TTcpApiPO extends CommonFields implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField("source_port")
    private int sourcePort;
    @TableField("source_host")
    private String sourceHost;
    @TableField("target_port")
    private int targetPort;
    @TableField("target_host")
    private String targetHost;

    private String description;

    @Override
    public String toString() {
        return "{" +
                "sourcePort=" + sourcePort +
                ", sourceHost='" + sourceHost + '\'' +
                ", targetPort=" + targetPort +
                ", targetHost='" + targetHost + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
