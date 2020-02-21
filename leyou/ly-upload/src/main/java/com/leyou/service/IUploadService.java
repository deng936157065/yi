package com.leyou.service;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadService {

    /**
     * 图片上传
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}
