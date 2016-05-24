package lib;

import static lib.BDD.*;
import lib.*;


class PayRoutines {

    static String gwPay(BDD bdd, def ctx, def bankId, def accessPlatform, def payUrl) {

        def orderId = "OD" + PayUtils.getSequenceNo()

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

        POST(bdd, payUrl) {
            r.body = params
        }
        EXPECT(bdd) {
            http.code = 200
        }
        //跳转到第三方支付
        def thirdPayUrl = PayUtils.getValueFromXML(bdd.respBody, "form1")
        GET(bdd, thirdPayUrl){
            r.server = thirdPayUrl
        }
        EXPECT(bdd) {
            http.code = 200
        }
        return orderId
    }

    //PC网页支付
    static String gwPayWeb(BDD bdd, def ctx, def bankId) {
        return gwPay(bdd, ctx, bankId, "1", ctx.gwPayWebUrl);
    }

    //手机网页支付
    static String gwPayWap(BDD bdd, def ctx, def bankId) {
        return gwPay(bdd, ctx, bankId, "2", ctx.gwPayWapUrl);
    }

    //手机App支付
    static String apiPaySDK(BDD bdd, def ctx, def bankId) {
        def orderId = "OD" + PayUtils.getSequenceNo()

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
            json.status = "SUCCESS"
            json."data" = NotEmpty
            json.closure = { json ->
                orderInfo = json.data.orderInfo
            }
        }
        orderInfo = "$orderInfo"

        println orderInfo
        return

        //模拟调起第三方支付SDK
        def sdkUrl = null
        if (bankId == "ALIPAY")
            sdkUrl = ctx.fakes.alipaySDKUrl
        else if (bankId == "WECHAT")
            sdkUrl = ctx.fakes.wechatSDKUrl

        POST(bdd, sdkUrl) {
            r.server = sdkUrl
            r.body = orderInfo
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = "SUCCESS"
        }

        return orderId
    }

    //PC扫码支付
    static String apiPayQRCode(BDD bdd, def ctx, def bankId) {
        def orderId = "OD" + PayUtils.getSequenceNo()

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
                accessPlatform: "1"
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        POST(bdd, ctx.apiPayQRCodeUrl) {
            r.body = params
        }
        def qrCode = null
        EXPECT(bdd) {
            http.code = 200
            json.status = "SUCCESS"
            json."data" = NotEmpty
            json.closure = { json ->
                qrCode = json.data.qrCode
            }
        }

        //模拟扫码
        def qrcodeUrl = null
        if (bankId == "ALIPAY")
            qrcodeUrl = qrCode
        else if (bankId == "WECHAT")
        //从二维码中提取支付链接
            qrcodeUrl = PayUtils.QRCode2Text(qrCode)

        GET(bdd, qrcodeUrl) {
            r.server = qrcodeUrl
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = "SUCCESS"
        }

        return orderId
    }

    //交易查询
    static void apiPayQuery(BDD bdd, def ctx, def orderId, def payStatus) {
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
            json.status = "SUCCESS"
            json."data" = NotEmpty
            json."data.payStatus" = payStatus
        }
    }

    //退款
    static void apiRefund(BDD bdd, def ctx, def orderId) {
        //交易查询API
        def params = [
                orderId : orderId,
                bgUrl   : ctx.bgUrl,
                appId   : ctx.appId,
                signType: "0",
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        GET(bdd, ctx.apiRefundUrl) {
            r.query = params
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = "SUCCESS"
        }
    }

    //退款查询
    static void apiRefundQuery(BDD bdd, def ctx, def orderId) {
        //交易查询API
        def params = [
                orderId : orderId,
                appId   : ctx.appId,
                signType: "0",
        ]
        def sign = PayUtils.signMD5(params, ctx.md5_key)
        params << [sign: sign]

        GET(bdd, ctx.apiRefundQueryUrl) {
            r.query = params
        }
        EXPECT(bdd) {
            http.code = 200
            json.status = "SUCCESS"
        }
    }

}