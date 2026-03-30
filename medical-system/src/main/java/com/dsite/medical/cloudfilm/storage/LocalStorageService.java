package com.dsite.medical.cloudfilm.storage;

import cn.hutool.core.io.FileUtil;
import com.dsite.medical.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地存储服务实现
 */
@Service("localStorageService")
public class LocalStorageService implements StorageService {

    /**
     * 存储根目录
     */
    @Value("${storage.local.path:./uploads}")
    private String storagePath;

    /**
     * 域名
     */
    @Value("${storage.local.domain:http://localhost:8080}")
    private String domain;

    @Override
    public String upload(InputStream inputStream, String fileName, String contentType) {
        try {
            // 生成唯一文件名
            String ext = FileUtil.extName(fileName);
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

            // 按日期组织目录
            String datePath = DateUtils.dateToStr(new Date(), DateUtils.YYYY_MM_DD);
            String relativePath = "images/" + datePath + "/" + newFileName;

            // 创建目录
            Path fullPath = Paths.get(storagePath, relativePath);
            Files.createDirectories(fullPath.getParent());

            // 写入文件
            FileOutputStream fos = new FileOutputStream(fullPath.toFile());
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            inputStream.close();

            return relativePath;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String filePath) {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            return new FileInputStream(fullPath.toFile());
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String filePath) {
        try {
            Path fullPath = Paths.get(storagePath, filePath);
            return Files.deleteIfExists(fullPath);
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getUrl(String filePath) {
        return domain + "/uploads/" + filePath;
    }
}
