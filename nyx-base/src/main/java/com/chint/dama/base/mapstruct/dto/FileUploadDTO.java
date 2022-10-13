package com.chint.dama.base.mapstruct.dto;

import lombok.Data;

/**
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-12-01 14:20
 */
@Data
public class FileUploadDTO {
    private String fileId;
    private String fileName;
    private String size;
    private String contentType;
    private byte[] content;
    private String fileType;
    private String columnId;
}
