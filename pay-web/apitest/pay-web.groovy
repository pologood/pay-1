

import static lib.BDD.*;
import lib.*;

def host = "test.cash.sogou.com"
def server = "http://$host"

def ctx = [
        appId: "6666",
        md5_key: "5c50cef44ef048d9892b7b0",
        pageUrl: "http://$host/notify/testBgUrl",
        bgUrl: "http://$host/notify/testBgUrl",
        gwPayWebUrl: "/gw/pay/web",
        gwPayWapUrl: "/gw/pay/wap",
        apiPaySDKUrl: "/api/pay/sdk",
        apiPayQRCodeUrl: "/api/pay/qrcode",
        apiPayQueryUrl: "/api/pay/query",
        apiRefundUrl: "/api/refund",
        apiRefundQueryUrl: "/api/refund/query",
        fakes: [
                alipaySDKUrl: "",
                alipayQRCodeUrl: "",
                wechatSDKUrl: ""
        ]
]

def bdd = NEW()

CONFIG(bdd, [
        debug: false,
        server: server,
        headers: [host: "$host"]
])

def orderId = PayRoutines.apiPaySDK(bdd, ctx, "WECHAT")
println orderId

return
PayRoutines.apiPayQuery(bdd, ctx, orderId, "CLOSED")

PayRoutines.apiRefund(bdd, ctx, orderId)


PayRoutines.apiRefundQuery(bdd, ctx, orderId)