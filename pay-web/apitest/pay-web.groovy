import static lib.BDD.*;
import lib.*;

//def host = "test.cash.sogou.com"
def host = "127.0.0.1"
def server = "http://$host"

def ctx = [
        appId            : "1000",
        md5_key          : "862653da5865293b1ec8cc",
        pageUrl          : "http://$host/notify/testBgUrl",
        bgUrl            : "http://$host/notify/testBgUrl",
        gwPayWebUrl      : "/gw/pay/web",
        gwPayWapUrl      : "/gw/pay/wap",
        apiPaySDKUrl     : "/api/pay/sdk",
        apiPayQRCodeUrl  : "/api/pay/qrcode",
        apiPayQueryUrl   : "/api/pay/query",
        apiRefundUrl     : "/api/refund",
        apiQueryRefundUrl: "/api/refund/query",
        fakeURL          : [
                TEST_ALIPAY: "http://test.stub.pay.sogou/api/alipay/directpay/mobile",
                TEST_WECHAT: "http://test.stub.pay.sogou/api/wechat/pay/mobile"
        ]
]

def bdd = NEW()

CONFIG(bdd, [
        debug  : false,
        server : server,
        headers: [host: "$host"]
])

//PC网页支付
["ALIPAY", "TENPAY"].each {
    def bankId = it
    println "[PayWeb]begin pay"
    def orderId = PayRoutines.gwPayWeb(bdd, ctx, "$bankId", null)
    println "[PayWeb]end pay, $orderId"
    println "[PayWeb]begin query, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayWeb]end query"
    println "[PayWeb]begin pay again, $orderId"
    orderId = PayRoutines.gwPayWeb(bdd, ctx, "$bankId", orderId)
    println "[PayWeb]end pay, $orderId"
    println "[PayWeb]begin refund, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId)
    println "[PayWeb]end refund"
    println "[PayWeb]begin query again, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayWeb]end query"
    println "[PayWeb]begin query refund, $orderId"
    PayRoutines.apiQueryRefund(bdd, ctx, orderId)
    println "[PayWeb]end query refund"
    println "[PayWeb]begin refund again, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId, "REFUND_ALREADY_DONE")
    println "[PayWeb]end refund"
}

println ""

//手机网页支付
["ALIPAY"].each {
    def bankId = it
    println "[PayWap]begin pay"
    def orderId = PayRoutines.gwPayWap(bdd, ctx, "$bankId", null)
    println "[PayWap]end pay, $orderId"
    println "[PayWap]begin query, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayWap]end query"
    println "[PayWap]begin pay again, $orderId"
    orderId = PayRoutines.gwPayWap(bdd, ctx, "$bankId", orderId)
    println "[PayWap]end pay, $orderId"
    println "[PayWap]begin refund, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId)
    println "[PayWap]end refund"
    println "[PayWap]begin query again, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayWap]end query"
    println "[PayWap]begin query refund, $orderId"
    PayRoutines.apiQueryRefund(bdd, ctx, orderId)
    println "[PayWap]end query refund"
    println "[PayWap]begin refund again, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId, "REFUND_ALREADY_DONE")
    println "[PayWap]end refund"
}

println ""

//手机App支付
["ALIPAY", "WECHAT"].each {
    def bankId = it
    println "[PaySDK]begin pay"
    def orderId = PayRoutines.apiPaySDK(bdd, ctx, "$bankId", null)
    println "[PaySDK]end pay, $orderId"
    println "[PaySDK]begin query, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PaySDK]end query"
    println "[PaySDK]begin pay again, $orderId"
    orderId = PayRoutines.apiPaySDK(bdd, ctx, "$bankId", orderId, "ORDER_ALREADY_DONE")
    println "[PaySDK]end pay, $orderId"
    println "[PaySDK]begin refund, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId)
    println "[PaySDK]end refund"
    println "[PaySDK]begin query again, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PaySDK]end query"
    println "[PaySDK]begin query refund, $orderId"
    PayRoutines.apiQueryRefund(bdd, ctx, orderId)
    println "[PaySDK]end query refund"
    println "[PaySDK]begin refund again, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId, "REFUND_ALREADY_DONE")
    println "[PaySDK]end refund"
}

println ""

//PC扫码支付
["ALIPAY", "WECHAT"].each {
    def bankId = it
    println "[PayQRCode]begin pay"
    def orderId = PayRoutines.apiPayQRCode(bdd, ctx, "$bankId", null)
    println "[PayQRCode]end pay, $orderId"
    println "[PayQRCode]begin query, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayQRCode]end query"
    println "[PayQRCode]begin pay again, $orderId"
    orderId = PayRoutines.apiPayQRCode(bdd, ctx, "$bankId", orderId, "ORDER_ALREADY_DONE")
    println "[PayQRCode]end pay, $orderId"
    println "[PayQRCode]begin refund, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId)
    println "[PayQRCode]end refund"
    println "[PayQRCode]begin query again, $orderId"
    PayRoutines.apiPayQuery(bdd, ctx, orderId)
    println "[PayQRCode]end query"
    println "[PayQRCode]begin query refund, $orderId"
    PayRoutines.apiQueryRefund(bdd, ctx, orderId)
    println "[PayQRCode]end query refund"
    println "[PayQRCode]begin refund again, $orderId"
    PayRoutines.apiRefund(bdd, ctx, orderId, "REFUND_ALREADY_DONE")
    println "[PayQRCode]end refund"
}

