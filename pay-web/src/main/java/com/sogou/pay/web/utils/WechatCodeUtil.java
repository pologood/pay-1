package com.sogou.pay.web.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.commons.codec.binary.Base64;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

/**
 * 
 * @author 用户平台事业部---高朋辉
 * @version 1.0
 * @date 2015/3/18 15:09
 */
public class WechatCodeUtil {

    public static String genWechatCode(String content) throws Exception {
        BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage
            qrcodeImg =
            MatrixToImageWriter.toBufferedImage(bitMatrix, new MatrixToImageConfig(0, 0xffffffff));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrcodeImg, "PNG", baos);
        baos.close();
        String base64 = Base64.encodeBase64String(baos.toByteArray());
        base64 = URLEncoder.encode(base64, "UTF-8");
        String wechatCode = String.format("data:image/png;base64,%s", base64);
        return wechatCode;
    }
}
