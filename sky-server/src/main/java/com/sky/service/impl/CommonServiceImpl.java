package com.sky.service.impl;

import com.sky.properties.AliOssProperties;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private AliOssProperties aliOssProperties;

    @Override
    public String upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + extension;
        String objectName = aliOssProperties.getUploadPath() + newFileName;
        return aliOssUtil.upload(file.getBytes(),objectName);
    }

}
