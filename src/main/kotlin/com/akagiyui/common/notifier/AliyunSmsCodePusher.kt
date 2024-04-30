package com.akagiyui.common.notifier

import com.akagiyui.common.notifier.exception.PushException
import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import darabonba.core.client.ClientOverrideConfiguration

/**
 * 阿里云短信验证码推送器
 * @author AkagiYui
 */
class AliyunSmsCodePusher(
    accessKeyId: String,
    accessKeySecret: String,
    private val signName: String,
    private val templateCode: String,
) {
    private val objectMapper = ObjectMapper()
    private val client: AsyncClient

    init {
        val provider = StaticCredentialProvider.create(
            Credential.builder().accessKeyId(accessKeyId).accessKeySecret(accessKeySecret).build()
        )
        client = AsyncClient.builder().credentialsProvider(provider).overrideConfiguration(
            ClientOverrideConfiguration.create().setEndpointOverride("dysmsapi.aliyuncs.com")
        ).build()
    }

    /**
     * 发送短信
     * @param phone 手机号
     * @param code 验证码
     */
    fun sendSms(phone: String, params: Map<String, String>): SendSmsResponse? {
        val templateParam = objectMapper.writeValueAsString(params)
        val sendSmsRequest = SendSmsRequest.builder().phoneNumbers(phone).signName(signName)
            .templateCode(templateCode).templateParam(templateParam).build()
        return try {
            val future = client.sendSms(sendSmsRequest)
            val response = future.get()
            if (response.body.code != "OK") {
                throw RuntimeException()
            }
            response
        } catch (e: Exception) {
            throw PushException("短信发送失败")
        }
    }
}
