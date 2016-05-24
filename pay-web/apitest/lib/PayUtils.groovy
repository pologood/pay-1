package lib

@Grab(group='com.google.zxing', module='core', version='3.2.1')
@Grab(group='com.google.zxing', module='javase', version='3.2.1')

import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import groovy.util.slurpersupport.GPathResult

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.time.*
import java.time.format.DateTimeFormatter
import org.apache.commons.codec.digest.DigestUtils


class PayUtils {

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

    static String getValueFromXML(GPathResult node, String key) {
        return node."**".find {
            it["@name"] == key
        }."@action"
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
            def hints = [(EncodeHintType.CHARACTER_SET): charset]
            text = new QRCodeReader().decode(bitmap, hints).getText()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return text
    }

}