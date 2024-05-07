package com.akagiyui.common

import com.akagiyui.common.crypto.DataSigner
import com.akagiyui.common.crypto.HmacSHA256DataSigner
import com.akagiyui.common.exception.GeetestCaptchaValidateException
import com.akagiyui.common.model.CaptchaType
import com.akagiyui.common.model.ClientType
import com.akagiyui.common.model.GeetestCaptchaV4ValidateResponse
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

/**
 * 极验验证码 V4 模板
 * @author AkagiYui
 */

class GeetestCaptchaV4Template(
    /**
     * 验证ID
     */
    private val captchaId: String,
    /**
     * 验证Key
     */
    private val captchaKey: String,
) {
    private val restTemplate = RestTemplate()
    private val url = "https://gcaptcha4.geetest.com/validate"
    private val objectMapper = ObjectMapper()
    private val signer: DataSigner = HmacSHA256DataSigner(captchaKey)

    /**
     * 二次验证
     *
     * @param lotNumber 流水号
     * @param captchaOutput 验证码输出
     * @param passToken 验证通过令牌
     * @param genTime 时间戳
     * @return 验证响应
     */
    fun validate(
        lotNumber: String,
        captchaOutput: String,
        passToken: String,
        genTime: String,
    ): GeetestCaptchaV4ValidateResponse {
        val params = LinkedMultiValueMap<String, String>().apply {
            add("lot_number", lotNumber)
            add("captcha_output", captchaOutput)
            add("pass_token", passToken)
            add("gen_time", genTime)
            add("sign_token", signer.sign(lotNumber))
        }
        val url = "${this.url}?captcha_id=$captchaId"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }
        val request = HttpEntity(params, headers)
        val response = restTemplate.postForObject(url, request, String::class.java)
        val result: JsonNode = objectMapper.readTree(response)
        require(result["status"].asText() == "success") { "Captcha validation failed" }
        if (result["result"].asText() != "success") {
            throw GeetestCaptchaValidateException(result["reason"].asText())
        }
        val data = result["captcha_args"]
        return GeetestCaptchaV4ValidateResponse(
            captchaType = CaptchaType.valueOf(data["used_type"].asText().uppercase()),
            userIp = data["user_ip"].asText(),
            lotNumber = data["lot_number"].asText(),
            scene = data["scene"].asText(),
            referer = data["referer"].asText(),
            ipType = data["ip_type"].asInt(),
            userInfo = data["user_info"].asText(),
            clientType = ClientType.valueOf(data["client_type"].asText().uppercase()),
            userAgent = data["ua"].asText(),
            failCount = data["fail_count"].asInt(),
        )
    }
}
