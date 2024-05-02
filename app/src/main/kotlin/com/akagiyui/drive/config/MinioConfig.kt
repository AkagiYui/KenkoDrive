package com.akagiyui.drive.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Minio 配置
 * @author AkagiYui
 */
@Configuration
class MinioConfig(
    @Value("\${application.storage.minio.endpoint}") private val endpoint: String,
    @Value("\${application.storage.minio.accessKey}") private val accessKey: String,
    @Value("\${application.storage.minio.secretKey}") private val secretKey: String,
) {

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
    }

}
