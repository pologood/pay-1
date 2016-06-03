package lib

@Grab(group = 'com.google.zxing', module = 'core', version = '3.2.1')
@Grab(group = 'com.google.zxing', module = 'javase', version = '3.2.1')
@Grab(group='org.apache.commons', module='commons-lang3', version='3.0')
@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.7.4')

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.apache.commons.lang3.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.time.*
import java.time.format.DateTimeFormatter
import org.apache.commons.codec.digest.DigestUtils
import java.net.URLDecoder
import java.net.URLEncoder


class PayUtils {

    static ObjectMapper objectMapper = new ObjectMapper()

    static String signMD5(Map<String, Object> params, String md5_key) {
        def keys = new ArrayList<String>(params.keySet())
        Collections.sort(keys)
        def sb = new StringBuilder()
        keys.each {
            key ->
                sb.append(key).append("=").append(params.get(key)).append("&")
        }
        sb.deleteCharAt(sb.length() - 1)
        sb.append(md5_key)
        return DigestUtils.md5Hex(sb.toString().getBytes("UTF-8"))
    }

    static boolean verifyMD5(Map<String, Object> params, String md5_key, String sign) {
        return sign.equals(signMD5(params, md5_key))
    }

    static String getSequenceNo() {
        LocalDateTime now = LocalDateTime.now()
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    }

    static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now()
        return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    }

    static String urlEncode(String data) {
        return URLEncoder.encode(data, "UTF-8")
    }

    static String urlDecode(String data) {
        return URLDecoder.decode(data, "UTF-8")
    }

    static String jsonSerialize(Map<String, Object> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (Exception ex) {
            throw new RuntimeException(ex)
        }
    }

    static String QRCode2Text(String qrCode) {
        def charset = "UTF-8"
        def prefix = "data:image/png;base64,"
        def text = null
        try {
            qrCode = qrCode.substring(prefix.length())
            qrCode = URLDecoder.decode(qrCode, charset)
            def qrcodeBytes = Base64.getDecoder().decode(qrCode.getBytes(charset))
            BufferedImage qrcodeImage = ImageIO.read(new ByteArrayInputStream(qrcodeBytes))
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(qrcodeImage)
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source))
            def hints = [(DecodeHintType.CHARACTER_SET): charset, (DecodeHintType.PURE_BARCODE): true]
            text = new QRCodeReader().decode(bitmap, hints).getText()
        } catch (Exception ex) {
            throw new RuntimeException(ex)
        }
        return text
    }

    static boolean isEmpty(String s){
        return StringUtils.isEmpty(s)
    }

    static String parseHost(String url){
        if(url.startsWith("http://")){
            url = url.substring(7)
        }else if(url.startsWith("https://")){
            url = url.substring(8)
        }
        return url.split("[:\\?/]", 2)[0]
    }

}