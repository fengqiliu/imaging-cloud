package com.dsite.medical.cloudfilm.storage;

import java.io.InputStream;

/**
 * 存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param contentType 内容类型
     * @return 文件存储路径
     */
    String upload(InputStream inputStream, String fileName, String contentType);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件流
     */
    InputStream download(String filePath);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean delete(String filePath);

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getUrl(String filePath);
}
