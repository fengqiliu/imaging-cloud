package com.dsite.medical.cloudfilm.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * MinIO对象存储服务实现
 */
@Service("minioStorageService")
public class MinioStorageService implements StorageService {

    @Value("${minio.url:http://localhost:9000}")
    private String url;

    @Value("${minio.accessKey:minioadmin}")
    private String accessKey;

    @Value("${minio.secretKey:minioadmin}")
    private String secretKey;

    @Value("${minio.bucketName:medical-film}")
    private String bucketName;

    private MinioClient getClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    private void createBucketIfNotExists() {
        try {
            MinioClient client = getClient();
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                client.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("初始化MinIO桶失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String fileName, String contentType) {
        try {
            createBucketIfNotExists();
            MinioClient client = getClient();

            // 生成唯一对象名
            String objectName = "images/" + System.currentTimeMillis() + "/" + fileName;

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );

            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到MinIO失败: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String filePath) {
        try {
            MinioClient client = getClient();
            return client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("从MinIO下载文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String filePath) {
        try {
            MinioClient client = getClient();
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException("从MinIO删除文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getUrl(String filePath) {
        return url + "/" + bucketName + "/" + filePath;
    }
}
