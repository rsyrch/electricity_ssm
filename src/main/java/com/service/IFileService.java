package com.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Date: 2018/6/16 22:15
 * @Description: 文件处理服务
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
