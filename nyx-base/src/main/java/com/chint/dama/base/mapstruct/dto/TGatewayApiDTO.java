package com.chint.dama.base.mapstruct.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class TGatewayApiDTO extends CommonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String path;
    private String serviceId;
    private String url;
    private Boolean retryable;
    private Boolean enabled;
    private Boolean stripPrefix;
    private String apiName;
    private String description;
    private String profileName;
}