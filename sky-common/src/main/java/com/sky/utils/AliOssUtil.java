package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.aliyun.oss.OSSClientBuilder;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class AliOssUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    public String upload(byte[] bytes,String objectName){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName,objectName,new ByteArrayInputStream(bytes));
        } catch (OSSException oe){
            System.out.println("Caught an OSSException,which meams your request made it to OSS,"
            + "but was rejected with an error response for some reason");
            System.out.println("Error Message:" + oe.getMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw oe;
        } catch(ClientException ce){
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw ce;
        } finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }

        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder.append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件上传到:{}", stringBuilder);
        return stringBuilder.toString();
    }
}
