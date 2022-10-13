package com.chint.dama.dao.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chint.dama.dao.CommonFields;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by Ruohong Cheng on 2021/11/22 17:08
 */
@Data
@Accessors(chain = true)
@TableName("t_gateway_api")
public class TGatewayApiPO extends CommonFields implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(value = "path")
    private String path;
    @TableField(value = "service_id")
    private String serviceId;
    @TableField(value = "url")
    private String url;
    @TableField(value = "retryable")
    private Boolean retryable;
    @TableField(value = "enabled")
    private Boolean enabled;
    @TableField(value = "strip_prefix")
    private Boolean stripPrefix;
    @TableField(value = "api_name")
    private String apiName;
    @TableField(value = "description")
    private String description;
    @TableField(value = "profilename")
    private String profileName;
}
