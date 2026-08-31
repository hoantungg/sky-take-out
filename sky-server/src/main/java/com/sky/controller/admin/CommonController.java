package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private CommonService commonService;
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info(
                "文件上传，原始文件名：{}",
                file.getOriginalFilename()
        );
        try {
            String filePath = commonService.upload(file);
            return Result.success(filePath);
        } catch (Exception e){
            log.error("文件上传失败", e);
            return Result.error(
                    MessageConstant.UPLOAD_FAILED
            );
        }
    }
}
