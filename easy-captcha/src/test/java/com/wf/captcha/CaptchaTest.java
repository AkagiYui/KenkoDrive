package com.wf.captcha;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试类
 * Created by 王帆 on 2018-07-27 上午 10:08.
 */
class CaptchaTest {

    @Test
    void test() {
        var specCaptcha = new SpecCaptcha(130, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        System.out.println(verCode);
        assertEquals(5, verCode.length());
    }

    @Test
    public void testGIf() throws Exception {
        /*for (int i = 0; i < 10; i++) {
            GifCaptcha gifCaptcha = new GifCaptcha();
            gifCaptcha.setLen(5);
            gifCaptcha.setFont(i, 32f);
            System.out.println(gifCaptcha.text());
            gifCaptcha.out(new FileOutputStream(new File("C:/Java/aa" + i + ".gif")));
        }*/
    }

    @Test
    public void testHan() throws Exception {
        /*for (int i = 0; i < 10; i++) {
            ChineseCaptcha chineseCaptcha = new ChineseCaptcha();
            System.out.println(chineseCaptcha.text());
            chineseCaptcha.out(new FileOutputStream(new File("C:/Java/aa" + i + ".png")));
        }*/
    }

    @Test
    public void testGifHan() throws Exception {
        /*for (int i = 0; i < 10; i++) {
            ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha();
            System.out.println(chineseGifCaptcha.text());
            chineseGifCaptcha.out(new FileOutputStream(new File("C:/Java/aa" + i + ".gif")));
        }*/
    }

    @Test
    public void testArit() throws Exception {
        /*for (int i = 0; i < 10; i++) {
            ArithmeticCaptcha specCaptcha = new ArithmeticCaptcha();
            specCaptcha.setLen(3);
            specCaptcha.setFont(i, 28f);
            System.out.println(specCaptcha.getArithmeticString() + " " + specCaptcha.text());
            specCaptcha.out(new FileOutputStream(new File("C:/Java/aa" + i + ".png")));
        }*/
    }

    @Test
    public void testBase64() throws Exception {
        /*GifCaptcha specCaptcha = new GifCaptcha();
        System.out.println(specCaptcha.toBase64(""));*/
    }

}
