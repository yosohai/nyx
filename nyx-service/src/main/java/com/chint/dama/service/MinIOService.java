package com.chint.dama.service;

import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class MinIOService {

    @Value("${chint.minio.enable:false}")
    Boolean enableMinIO;

    @Value("${chint.minio.ak}")
    String accessKey;

    @Value("${chint.minio.sk}")
    String secretKey;

    @Value("${chint.minio.buckets}")
    String buckets;

    @Value("${chint.minio.endpoint}")
    String endPoint;

    public boolean isEnable() {
        return Boolean.TRUE.equals(enableMinIO);
    }

    private String getDate(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MMdd");
        return sf.format(new Date());
    }
    public String upload(byte[] content, String fileName, String columnId) throws MinioException, IOException,
			NoSuchAlgorithmException, InvalidKeyException {
        if(StringUtils.isBlank(columnId)){
            columnId = "DEFAULT_CODE";
        }
        String path = columnId + "/" + getDate() +  "/" + UUID.randomUUID().toString() + ext(fileName);

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endPoint)
                        .credentials(accessKey, secretKey)
                        .build();
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(buckets)
                .object(path)
                .stream(new ByteArrayInputStream(content), content.length, -1)
                .build();
        minioClient.putObject(putObjectArgs);

        return path;
    }

    private String ext(String fileName) {
        if(StringUtils.isBlank(fileName)){
            return "";
        }
        int off = fileName.lastIndexOf(".");
        if(off>=0){
            return fileName.substring(off);
        }
        return "";
    }

    public byte[] loadBytes(String path)  {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endPoint)
                        .credentials(accessKey, secretKey)
                        .build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(buckets)
                        .object(path)
                        .build())) {
            while (true){
                int len = stream.read(buffer);
                if(len==-1){
                    break;
                }
                bos.write(buffer,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
}
