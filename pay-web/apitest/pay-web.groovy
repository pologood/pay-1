

import static lib.BDD.*;
import lib.*;

def host = "test.cash.sogou.com"
def server = "http://$host"

def ctx = [
        appId: "1000",
        md5_key: "862653da5865293b1ec8cc",
        pageUrl: "http://$host/notify/testBgUrl",
        bgUrl: "http://$host/notify/testBgUrl",
        gwPayWebUrl: "/gw/pay/web",
        gwPayWapUrl: "/gw/pay/wap",
        apiPaySDKUrl: "/api/pay/sdk",
        apiPayQRCodeUrl: "/api/pay/qrcode",
        apiPayQueryUrl: "/api/pay/query",
        apiRefundUrl: "/api/refund",
        apiRefundQueryUrl: "/api/refund/query",
        fakeURL: [
                TEST_ALIPAY: "http://test.stub.pay.sogou/api/alipay/directpay/mobile",
                TEST_WECHAT: "http://test.stub.pay.sogou/api/wechat/pay/mobile"
        ]
]

def bdd = NEW()

CONFIG(bdd, [
        debug: false,
        server: server,
        headers: [host: "$host"]
])

def orderId = PayRoutines.apiPayQRCode(bdd, ctx, "WECHAT")
println orderId

return
PayRoutines.apiPayQuery(bdd, ctx, orderId, "CLOSED")

PayRoutines.apiRefund(bdd, ctx, orderId)


PayRoutines.apiRefundQuery(bdd, ctx, orderId)