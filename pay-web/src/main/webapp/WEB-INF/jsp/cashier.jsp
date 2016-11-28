<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>搜狗支付中心收银台</title>
    <link rel="stylesheet" type="text/css" href="/static/css/base.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/cash.css"/>    
    <script type="text/javascript" src="/static/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="/static/js/index.js"></script>
</head>

<body>
    <div class="top-nav">
    <div class="logo-area clearfix">
      <div class="logo "></div>
      <div class="logo-title ">搜狗支付中心</div>
      <div class="logo-line "></div>
      <div class="logo-aside hm">黑马定制</div>

      <div class="logo-aside wm"><a href="http://pay.sogou.com" target="_blank">联系我们</a></div>

    </div>
  </div>
<script type="text/javascript">
    var interval;

    function getQRCode(channelCode) {
        var params = {};
        $("#payForm input").each(
                function () {
                    var target = $(this);
                    params[target.prop("name")] = target.prop("value");
                }
        );
        params["channelCode"] = channelCode;
        params["accessPlatform"] = 4;
        $.ajax({
            url: "/api/pay/qrcode",
            type: "POST",
            data: params,
            dataType: "json"
        }).done(
                function (ret, status, jqXHR) {
                    var rsp = jqXHR.responseJSON;
                    if (rsp.status == "SUCCESS") {
                        $(".QR-fail").hide();
                        $("#QRCode-" + channelCode).prop("src", rsp.data.qrCode);
                        clearInterval(interval);
                        if (channelCode == "WECHAT") {
                            interval = setInterval(function () {
                                getPayStatus();
                            }, 3000);
                        }
                    } else {
                        $(".QR-content").html("<p>" + rsp.message + "</p><br><a href='#'>查看订单</a>");
                        $(".QRCode-ALIPAY").hide();
                        $(".QRCode-WECHAT").hide();
                        $(".QR-fail").show();
                    }
                }
        ).fail(
                function (ret, status, jqXHR) {
                    $(".QR-content").html("<p>请求失败，请重新支付</p><br><a href='#'>查看订单</a>");
                    $(".QRCode-ALIPAY").hide();
                    $(".QRCode-WECHAT").hide();
                    $(".QR-fail").show();
                }
        );
    }

    function getPayStatus() {
        $.ajax({
            url: "/api/pay/query",
            type: "GET",
            data: {
                orderId: "${queryParams.orderId}",
                appId: "${queryParams.appId}",
                fromCashier: "${queryParams.fromCashier}",
                signType: "${queryParams.signType}",
                sign: "${queryParams.sign}"
            },
            dataType: "json"
        }).success(function (ret, status, jqXHR) {
            var rsp = jqXHR.responseJSON;
            if (rsp.status == "SUCCESS") {
                var payStatus = rsp.data.payStatus;
                var payReqId = rsp.data.payReqId;
                if (payStatus == "SUCCESS" || payStatus == "REFUND") {
                    window.location.href = "/notify/websync/wechat?out_trade_no="
                            + payReqId + "&result_code=" + payStatus;
                }
            }
        });
    }

    $(function () {
        $(".styleTitle").click(function () {
            $(this).addClass("on").siblings(".styleTitle").removeClass("on");
            $(this).next(".pay-platform").show().siblings(".pay-platform").hide();
        });
        $(".bankDown").click(function (e) {
            e.preventDefault();
            $(this).hide().siblings(".bankUp").show();
            $(this).parent().parent().find('.dotb-bank-card').css("height", "auto");
        });
        $(".bankUp").click(function (e) {
            e.preventDefault();
            $(this).hide().siblings(".bankDown").show();
            $(this).parent().parent().find('.dotb-bank-card').css("height", "74px");
        });
        $(".bankDown2").click(function (e) {
            e.preventDefault();
            $(this).hide().siblings(".bankUp2").show();
            $(this).parent().parent().find('.dotb-company').css("height", "auto");
        });
        $(".bankUp2").click(function (e) {
            e.preventDefault();
            $(this).hide().siblings(".bankDown2").show();
            $(this).parent().parent().find('.dotb-company').css("height", "74px");
        });
        $(".wxClose").click(function(e){
            e.preventDefault();
            $(".wxArea").hide();
            $(".mask").hide();
        })
        // $(".sm").change(function () {
        //     if ($(this).attr("value") == "ALIPAY" && $(this).attr("data-flag") == "QRCODE") {
        //         $(".QRCode-WECHAT").hide();
        //         $(".QRCode-ALIPAY").show();
        //         $(".paySubmit").css("display", "none");
        //     } else if ($(this).attr("value") == "WECHAT") {
        //         $(".QRCode-ALIPAY").hide();
        //         $(".QRCode-WECHAT").show();
        //         $(".paySubmit").css("display", "none");
        //     } else {
        //         $(".paySubmit").show();
        //         $(".QRCode-fail").hide();
        //         $(".QRCode-WECHAT").hide();
        //         $(".QRCode-ALIPAY").hide();
        //     }
        // });
        var i = 0;
        $("[data-target='paySubmit']").click(function (e) {
            for (j=0; j< $('.sm').length; j++) {  
                if ($('.sm')[j].checked) {
                    if($('.sm')[j].value == "WECHAT") {
                        $(".wxArea").show();
                        $(".mask").show();
                        getWxCode('WECHAT');
                        function getWxCode(channelCode){
                            var params = {};
                            $("#payForm input").each(
                                function () {
                                    var target = $(this);
                                    params[target.prop("name")] = target.prop("value");
                                }
                            );
                            params["channelCode"] = channelCode;
                            params["accessPlatform"] = 4;
                            $.ajax({
                                url: "/api/pay/qrcode",
                                type: "POST",
                                data: params,
                                dataType: "json"
                            }).done(
                            		function (ret, status, jqXHR) {
                                        var rsp = jqXHR.responseJSON;
                                        if (rsp.status == "SUCCESS") {
                                            $(".wxPay").prop("src", rsp.data.qrCode);
                                        }
                                    }
                            )
                        }
                    } else {
                        $('#toPayTip').modal('show');
                        $("#payForm").attr("target", "newWin" + i);
                        $("#payForm").submit();
                        i++;
                        return false;
                    }
                }  
            }
        });
         $("[data-flag='QRCODE']").click(function (e) {
            getQRCode($(this).prop("value"));
        });
    });
</script>
<form action="/gw/pay/web" method="post" name="payForm" id="payForm">
    <c:forEach items="${payParams}" var="entry">
        <input type="hidden" name="${entry.key}" value="${entry.value}"/>
    </c:forEach>
    <div class="hm_style">
        <div class="wrapper">
            <div class="dog"></div>
            <div class="payM pay-info clearfix ">
                <div class="pay-details">

                    <dl class="dlsett pay-details-goods">
                        <dt class="pay-details-title">商品名称：</dt>
                        <dd>${payParams.productName}</dd>
                    </dl>
                    <dl class="dlsett">
                        <dt class="pay-details-title">订单编号 </dt>
                        <dd>${payParams.orderId}</dd>
                    </dl>
                    <dl class="dlsett">
                        <dt class="pay-details-title pay-details-collection">收款方：</dt>
                        <dd>${companyName}</dd>
                    </dl>
                    <div class=" pay-money">
                        <dt class="money-title">应付金额：</dt>
                        <dd><span class="money">${payParams.orderAmount}</span>元</dd>
                    </div>
                </div>

            </div>

            <div class="payM">
                <dl class="payStyle">
                    <div class="pay-title">选择支付方式</div>
                    <dd>
                        <c:if test="${fn:length(thirdPayList) != 0}">
                            <div class="dotb">
                                <h3>第三方支付</h3>
                                <ul class="clearfix">
                                    <c:forEach var="thirdPay" items="${thirdPayList}" varStatus="status">
                                        <li>
                                            <label>
                                                <input type="radio" name="channelCode" value="${thirdPay.channelCode}"
                                                       <c:if test="${status.count==1}">checked="checked"</c:if> class="sm"
                                                       data-flag="THIRD"/>
                                                <img src="${thirdPay.logo}"/>
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:if>
                        <c:if test="${fn:length(qrCodeList) != 0}">
                            <div class="dotb dotb-code">
                                <h3>扫码支付</h3>
                                <ul class="clearfix">
                                    <c:forEach var="qrCodePay" items="${qrCodeList}">
                                        <li>
                                            <label>
                                                <input type="radio" name="channelCode" value="${qrCodePay.channelCode}"
                                                       class="sm"
                                                       data-flag="QRCODE"/>
                                                <img src="${qrCodePay.logo}"/>
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
<!--                                <div class="QRCode-WECHAT">
                                    <img id="QRCode-WECHAT" src="/static/img/loading.gif">
                                </div>
                                <div class="QRCode-fail">
                                    <div class="QRCode-content">
                                        <p></p>

                                        <p>请勿重复提交</p>
                                        <br>
                                        <a href="#">查看订单</a>
                                    </div>
                                </div>  -->
                            </div>
                        </c:if>
                        <c:if test="${fn:length(bankList) != 0}">
                            <div class="dotb dotb-bank-card">
                                <h3>银行卡支付</h3>
                                <ul class="clearfix">
                                    <c:forEach var="bankPay" items="${bankList}">
                                        <li>
                                            <label>
                                                <input type="radio" name="channelCode" value="${bankPay.channelCode}" class="sm"
                                                       data-flag="BANK"/>
                                                <img src="${bankPay.logo}"/>
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                                <c:if test="${bankList== null || fn:length(bankList) > 4}">
                                    <a href="javascript:void(0)" class="bankDown">更多</a>
                                    <a href="javascript:void(0)" class="bankUp">收起</a>
                                </c:if>
                            </div>
                        </c:if>
                        <c:if test="${fn:length(b2bList) != 0}">
                            <div class="dotb dotb-company">
                                <h3>企业网银支付</h3>
                                <ul class="clearfix">
                                    <c:forEach var="b2bPay" items="${b2bList}">
                                        <li>
                                            <label>
                                                <input type="radio" name="channelCode" value="${b2bPay.channelCode}" class="sm"
                                                       data-flag="BANKB2B"/>
                                                <img src="${b2bPay.logo}"/>
                                            </label>
                                        </li>
                                    </c:forEach>
                                </ul>
                                <c:if test="${b2bList== null || fn:length(b2bList) > 4}">
                                    <a href="javascript:void(0)" class="bankDown2">更多</a>
                                    <a href="javascript:void(0)" class="bankUp2">收起</a>
                                </c:if>
                            </div>
                        </c:if>
                    </dd>
                    <div class="clearfix">
                        <a href="javascript:void(0)" class="paySubmit" target="blank" data-target="paySubmit"><span>确认付款</span></a>
                    </div>
                </dl>

                <dl class="question-list">
                    <dd class="question-title">支付问题</dd>
                    <dd class="question-item">
                        <div class="question-q">1、进行网上在线支付前，银行卡需要办理开通网上银行手续吗？</div>
                        <div class="question-a">普通银行卡的“网银支付”，需要事先开通网上银行（具体程序请您咨询相关银行）。</div>
                    </dd>
                    <dd class="question-item">
                        <div class="question-q">2、银行支付有限额要求吗？</div>
                        <div class="question-a">
                            为了保证在线支付的安全性，银行会根据各个商户、消费者情况的不一致进行支付金额限制，由于各银行的限额标准并不完全一致，并且会随国家政策进行调整，请您在办理网上银行时，问明限额标准，或者拨打银行热线电话进行咨询。
                        </div>
                    </dd>
                    <dd class="question-item">
                        <div class="question-q">3、没有开通网上银行怎么购物？</div>
                        <div class="question-a">如果没有开通网上银行，建议使用支付平台支付或扫码支付。</div>
                    </dd>
                </dl>
            </div>
            <div class="copyright">
                <a class="copyright-item" href="http://fuwu.sogou.com" target="_blank">商务合作</a>
                <span>|</span>
                <a class="copyright-item" href="http://pinyin.sogou.com" target="_blank">拼音输入法</a>
                <span>|</span>
                <a class="copyright-item" href="http://ie.sogou.com" target="_blank">搜狗浏览器 </a>
                <span>|</span>
                <a class="copyright-item" href="http://123.sogou.com" target="_blank">网址导航 </a>
                <span>|</span>
                <a class="copyright-item" href="http://wan.sogou.com" target="_blank">游戏中心 </a>
                <span>|</span>
                <a class="copyright-item" href="http://hr.sogou.com" target="_blank">诚聘英才</a>
                <br/>
                <br/>
                北京搜狗网络技术有限公司 版权所有
            </div>
        </div>
    </div>
</form>
<!-- 支付弹窗 -->
<div class="modal hide to-pay-tip" id="toPayTip" data-backdrop="static">
    <div class="modal-body">
        <div class="modal-header clearfix">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span>×</span></button>
        </div>
        <div class="modal-content">
            <div class="pay-content clearfix">
                <p>付款完成前请不要关闭此窗口。完成付款后请根据您的情况点击下面的按钮。</p>

                <div class="success le">
                    <a href="${payParams.pageUrl}" class="btn rg"><span>已经完成付款</span></a>
                </div>
                <div class="fail le">
                    <a href="" class="btn le def" data-dismiss="modal"><span>更换支付方式</span></a>
                </div>
                <p align="center">温馨提示：如果是支付限额问题，请选择其他支付方式。</p>
            </div>
        </div>
    </div>
</div>


<!-- 遮罩层 -->
<div class="mask"></div>
<!-- 微信弹窗 --> 
<div class="wxArea">
    <a href="javascript:void(0)" class="wxClose"></a>
    <dt>
        <img class="wxPay" src="" alt="">
    </dt>
    <dd></dd>
</div>
</body>
</html>