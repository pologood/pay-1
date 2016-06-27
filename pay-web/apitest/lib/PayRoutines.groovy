package lib;

import static lib.BDD.*;
import lib.*;
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

class PayRoutines {

    static String gwPay(BDD bdd, def ctx, def bankId, def accessPlatform, def payUrl, def orderId, def status) {
        def payAgain = false
        if (PayUtils.isEmpty(orderId))
            orderId = "OD" + PayUtils.getSequenceNo()
        else
            payAgain = true

        if (!bankId.startsWith("TEST_"))
            bankId = "TEST_" + bankId

        //网页支付API
        def params = [
                version       : "v1.0",
                pageUrl       : ctx.pageUrl,
                bgUrl         : ctx.bgUrl,
                orderId       : orderId,
                orderAmount   : "0.01",
                orderTime     : PayUtils.getCurrentTime(),
                productName   : "测试商品",
                productNum    : "1",
                bankId        : bankId,
                appId         : ctx.appId,
                signType      : "0",
                accessPlatform: accessPlatform
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        def thirdPayUrl

        POST(bdd, payUrl) {
            r.body = params
        }
        EXPECT(bdd) {
            http.code = 200
            xml.closure = { docs ->
                thirdPayUrl = walkGPath(docs, "body.form.@action")
            }
        }

        //如果为重复支付，直接返回
        if (payAgain) {
            assert PayUtils.isEmpty(thirdPayUrl)
            return orderId
        }

        //跳转到第三方支付
        String[] parts = thirdPayUrl.split("\\?")
        def thirdPayPath = parts[0]
        def thirdPayParams = parts[1]
        def host = PayUtils.parseHost(thirdPayPath)
        def header_host = [host: "$host"]

        POST(bdd, thirdPayPath) {
            r.server = thirdPayPath
            r.body = thirdPayParams
            r.requestContentType = URLENC
            r.headers = header_host
        }
        EXPECT(bdd) {
            http.code = 200
        }

        return orderId
    }

    //PC网页支付
    static String gwPayWeb(BDD bdd, def ctx, def bankId, def orderId, def status = "SUCCESS") {
        return gwPay(bdd, ctx, bankId, "1", ctx.gwPayWebUrl, orderId, status);
    }

    //手机网页支付
    static String gwPayWap(BDD bdd, def ctx, def bankId, def orderId, def status = "SUCCESS") {
        return gwPay(bdd, ctx, bankId, "2", ctx.gwPayWapUrl, orderId, status);
    }

    //手机App支付
    static String apiPaySDK(BDD bdd, def ctx, def bankId, def orderId, def status = "SUCCESS") {
        def payAgain = false
        if (PayUtils.isEmpty(orderId))
            orderId = "OD" + PayUtils.getSequenceNo()
        else
            payAgain = true

        if (!bankId.startsWith("TEST_"))
            bankId = "TEST_" + bankId

        //SDK支付API
        def params = [
                version       : "v1.0",
                pageUrl       : ctx.pageUrl,
                bgUrl         : ctx.bgUrl,
                orderId       : orderId,
                orderAmount   : "0.01",
                orderTime     : PayUtils.getCurrentTime(),
                productName   : "测试商品",
                productNum    : "1",
                bankId        : bankId,
                appId         : ctx.appId,
                signType      : "0",
                accessPlatform: "3"
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        POST(bdd, ctx.apiPaySDKUrl) {
            r.body = params
        }

        def orderInfo = null
        EXPECT(bdd) {
            http.code = 200
            json.status = status
            json.closure = { json ->
                orderInfo = json.data.orderInfo
            }
        }

        //如果为重复支付，直接返回
        if (payAgain) {
            assert PayUtils.isEmpty(orderInfo)
            return orderId
        }

        if (bankId == "TEST_ALIPAY")
            orderInfo = PayUtils.urlEncode(orderInfo)
        else if (bankId == "TEST_WECHAT")
            orderInfo = PayUtils.jsonSerialize(orderInfo)

        def sdkUrl = ctx.fakeURL["$bankId"]
        def host = PayUtils.parseHost(sdkUrl)
        def header_host = [host: "$host"]

        //模拟调起第三方支付SDK
        POST(bdd, sdkUrl) {
            r.server = sdkUrl
            r.body = orderInfo
            r.requestContentType = TEXT
            r.headers = header_host
        }
        EXPECT(bdd) {
            http.code = 200
            json.code = 0
        }

        return orderId
    }

    //PC扫码支付
    static String apiPayQRCode(BDD bdd, def ctx, def bankId, def orderId, def status = "SUCCESS") {
        def payAgain = false
        if (PayUtils.isEmpty(orderId))
            orderId = "OD" + PayUtils.getSequenceNo()
        else
            payAgain = true

        if (!bankId.startsWith("TEST_"))
            bankId = "TEST_" + bankId

        //SDK支付API
        def params = [
                version       : "v1.0",
                pageUrl       : ctx.pageUrl,
                bgUrl         : ctx.bgUrl,
                orderId       : orderId,
                orderAmount   : "0.01",
                orderTime     : PayUtils.getCurrentTime(),
                productName   : "测试商品",
                productNum    : "1",
                bankId        : bankId,
                appId         : ctx.appId,
                signType      : "0",
                accessPlatform: "4"
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        POST(bdd, ctx.apiPayQRCodeUrl) {
            r.body = params
        }

        def qrCode = null
        EXPECT(bdd) {
            http.code = 200
            json.status = status
            json.closure = { json ->
                qrCode = json.data.qrCode
            }
        }

        //如果为重复支付，直接返回
        if (payAgain) {
            assert PayUtils.isEmpty(qrCode)
            return orderId
        }

        //模拟扫码
        def qrcodeUrl = null
        if (bankId == "TEST_ALIPAY")
            qrcodeUrl = qrCode
        else if (bankId == "TEST_WECHAT")
        //从二维码中提取支付链接
            qrcodeUrl = PayUtils.QRCode2Text(qrCode)

        String[] parts = qrcodeUrl.split("\\?")
        def qrcodePath = parts[0]
        def qrcodeParams = parts[1]
        def host = PayUtils.parseHost(qrcodePath)
        def header_host = [host: "$host"]

        POST(bdd, qrcodePath) {
            r.server = qrcodePath
            r.body = qrcodeParams
            r.requestContentType = URLENC
            r.headers = header_host
        }
        EXPECT(bdd) {
            http.code = 200
            json.code = 0
        }

        return orderId
    }

    //交易查询
    static void apiPayQuery(BDD bdd, def ctx, def orderId, def status = "SUCCESS") {
        //交易查询API
        def params = [
                orderId : orderId,
                appId   : ctx.appId,
                signType: "0",
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        GET(bdd, ctx.apiPayQueryUrl) {
            r.query = params
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = status
        }
    }

    //退款
    static void apiRefund(BDD bdd, def ctx, def orderId, def status = "SUCCESS") {
        //交易查询API
        def params = [
                orderId : orderId,
                bgUrl   : ctx.bgUrl,
                appId   : ctx.appId,
                signType: "0",
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        POST(bdd, ctx.apiRefundUrl) {
            r.query = params
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = status
        }
    }

    //退款查询
    static void apiQueryRefund(BDD bdd, def ctx, def orderId, def status = "SUCCESS") {
        //交易查询API
        def params = [
                orderId : orderId,
                appId   : ctx.appId,
                signType: "0",
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        GET(bdd, ctx.apiQueryRefundUrl) {
            r.query = params
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = status
        }
    }

}