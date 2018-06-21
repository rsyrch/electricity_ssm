package com.service.impl;

import com.google.common.collect.Lists;
import com.service.IFileService;
import com.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Date: 2018/6/16 22:16
 * @Description: 文件处理
 */
@Service(value = "iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    /**
     * Description: 根据文件名和path上传文件, 返回文件名
     * CreateDate: 2018/6/16 22:46
     * 
    */
    public String upload(MultipartFile file, String path) {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 上传文件名
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("文件上传：文件名：{}, 上传路径：{}, 新文件名：{}", fileName, path, uploadFileName);

        // 声明目录
        File fileDir = new File(path);
        // 目录不存在
        if(!fileDir.exists()) {
            // 赋予可写权限
            fileDir.setWritable(true);
            // 创建目录
            fileDir.mkdirs();
        }

        // 创建文件
        File targetFile = new File(path, uploadFileName);
        try {
            file.transferTo(targetFile);

            // 上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            // 上传成功,删除本地文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
