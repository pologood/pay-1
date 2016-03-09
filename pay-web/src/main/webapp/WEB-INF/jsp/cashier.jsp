<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搜狗支付中心收银台</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/base.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/index.css"/>
    <!--HM开始-->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/hm/cssfix.css">
<!--[if lt IE 9]>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/hm/iefix.css">
<![endif]-->
    <!--HM结束-->
<script>
	var interval;
	<c:if test="${!empty commonMap.payDetailId}">
	window.onload = function(){
		interval = setInterval(function(){checkPayStatus()}, 3000);
	}
	</c:if>
	
	function getQRCode(channelcode){
		$("#channelType").val("3");
		if("WECHAT" == channelcode){
			$.ajax({ 
		            type: "post", 
		            url: "${pageContext.request.contextPath}/pay/getQrCode",
		            data:{channelCode:channelcode,
		            	  appId:$("input[name='appId']").val(),
		            	  accessPlatform:$("input[name='accessPlatform']").val(),
		            	  bankCardType:$("input[name='bankCardType']").val(),
		            	  orderAmount:$("input[name='orderAmount']").val(),
		            	  payId:$("input[name='payId']").val(),
		            	  userIp:$("input[name='userIp']").val(),
		            	  productName:$("input[name='productName']").val(),
		            	  channelType:"3"
		            	  },
		            dataType: "json", 
		            success: function (data) {
		            	//转换成json
		                var json = eval(data);
		            	if(json.status == "SUCCESS"){
			         	  $(".QR-fail").hide();
			         	   var syImg = document.getElementById('syImg');
			         	   var detailWxImg = document.getElementById('detailWxImg'); 
			         	   syImg.src = json.data.qrCode;
			         	   detailWxImg.src = json.data.qrCode;
							clearInterval(interval);
							interval = setInterval(function(){checkPayStatus()}, 3000);
		            	} else {
			                $(".QR-content").html("<p>"+json.message+"</p><br><a href='#'>查看订单</a>");
		            		$(".QR-fail").show();
		        			$(".QR-Code-WeChat").hide();
		        			$(".QR-Code-Ali").hide();
		            	}
		            }, 
		            error: function (XMLHttpRequest, textStatus, errorThrown) {
		            	$(".QR-Code-Ali").hide();
		            	$(".QR-Code-WeChat").hide();
		            	$(".QR-content").html("<p>请求失败，请重新支付</p><br><a href='#'>查看订单</a>");
	            		$(".QR-fail").show();
					} 
		        });
		} else {
			$.ajax({ 
	            type: "post", 
	            url: "${pageContext.request.contextPath}/pay/getQrCode",
	            data:{channelCode:channelcode,
	            	  appId:$("input[name='appId']").val(),
	            	  accessPlatform:$("input[name='accessPlatform']").val(),
	            	  bankCardType:$("input[name='bankCardType']").val(),
	            	  orderAmount:$("input[name='orderAmount']").val(),
	            	  payId:$("input[name='payId']").val(),
	            	  userIp:$("input[name='userIp']").val(),
	            	  productName:$("input[name='productName']").val(),
	            	  channelType:"3"
	            	  },
	            dataType: "json", 
	            success: function (data) {
	            	//转换成json
	                var json = eval(data);
	            	if(json.status == "SUCCESS"){
	            		$(".QR-fail").hide();
	            		document.getElementById("if1").setAttribute("src",json.data.qrCode);
	            	} else {
	            		$(".QR-Code-Ali").hide();
	            		$(".QR-content").html("<p>"+json.message+"</p><br><a href='#'>查看订单</a>");
	            		$(".QR-fail").show();
	            	}
	            }, 
	            error: function (XMLHttpRequest, textStatus, errorThrown) {
	            	$(".QR-Code-Ali").hide();
	            	$(".QR-content").html("<p>请求失败，请重新支付</p><br><a href='#'>查看订单</a>");
            		$(".QR-fail").show();
				} 
	        });
		}
	}
	
	function checkPayStatus(){
		var payReqId = $("input[name='payDetailId']").val();
		if(payReqId == "")
			return;
		$.ajax({ 
            type: "post", 
            url: "${pageContext.request.contextPath}/notify/status/wechat",
            data:{payReqId:payReqId,
            	     appId:$("input[name='appId']").val()
          	     },
            dataType: "json", 
            success: function (data) {
            	//转换成json
                var json = eval(data);
            	if(json.status == "SUCCESS"){
            		var payStatus = json.data.payStatus;
            		if(payStatus == "SUCCESS" || payStatus == "REFUND"){
            			window.location.href="${pageContext.request.contextPath}/notify/websync/wechat?out_trade_no="
								+json.data.payReqId + "&result_code=" + payStatus;
            		}
            	}
            }
        });
	}
	
	function changeChannelType(channelType){
		$("#channelType").val(channelType);
	}
	
	function goSubmit(){
		$("#payForm").submit();
	}
</script>
</head>

<!--HM class-->
<body class="hm_style">
<div class="header">
</div>
<form action="${pageContext.request.contextPath}/pay/doCashierPay" method="post" name="payForm" id="payForm">
<c:forEach items="${commonMap}" var="entry">  
       <input type="hidden" name="${entry.key}" value="${entry.value}"/>
</c:forEach>
<input id="channelType" type="hidden" name="channelType" value="2"/>
<div class="wrapper">
	<div class="logo-area clearfix">
		<div class="logo le"></div>
		<div class="logo-title le">搜狗支付中心</div>
		<div class="logo-line le"></div>
        <!--HM class hm-->
		<div class="logo-aside le hm">收银台</div>
        <!--HM开始-->
		<div class="logo-aside wm"><a href="http://pay.sogou" target="_blank">了解我们</a></div>
        <!--HM结束-->
	</div>
<div class="payM pay-info clearfix ">
		<div class="pay-icon le"></div>
		<div class="pay-details le ">
			
			<dl class="dlsett">
				<dt class="pay-details-title">订单编号：</dt>
				<dd>${orderId }</dd>
			</dl>
			<dl class="dlsett">
				<dt class="pay-details-title">订单信息：</dt>
				<%-- <dd>${commonMap.productName }</dd> --%>
				<dd>${commonMap.productName }</dd>
			</dl>
			<dl class="dlsett">
				<dt class="pay-details-title">收款方：</dt>
				<dd>${commonMap.companyName }</dd>
			</dl>
			<div class=" pay-money">
				<dt class="money-title">应付金额：</dt>
				<dd><span class="money">${commonMap.orderAmount }</span>元</dd>
			</div>
		</div>
		
		<c:if test="${!empty commonMap.payDetailId}">
		<div class="pay-code rg ">
			<div class="pay-img">
				<img id="detailWxImg" style="width:100%;" src="${qrCode}"/>
				<img src="${pageContext.request.contextPath}/static/img/wx.jpg"/>
				<dd class="exploreInfo">微信二维码暂不支持IE7及以下版本</dd>
			</div>
		</div>
		</c:if>
	</div>
	<div class="pay-title">选择支付方式</div>
	<div class="payM">
		<dl class="dlsett payStyle">
			<dd>
				<c:if test="${fn:length(payOrgList) != 0}">
				<div class="dotb">
					<h3>支付平台</h3>
					<ul>
						<c:forEach var="commonPay" items="${payOrgList}" varStatus="status">
							<li>
								<label>
									<input type="radio" name="bankId" value="${commonPay.channelCode }" <c:if test="${status.count==1}">checked="checked"</c:if> class="sm" data-flag="THIRD" onclick="changeChannelType(2)"/>
									<img src="${commonPay.logo}"/>
								</label>
							</li>
						</c:forEach>
					</ul>
				</div>
				</c:if>
				<c:if test="${fn:length(scanCodeList) != 0}">
				<div class="dotb">
					<h3>扫码支付<span>（推荐支付宝、微信用户使用）</span></h3>
					<ul>
						<c:forEach var="commonPay" items="${scanCodeList}">
							<li>
								<label>
									<input onclick="getQRCode('${commonPay.channelCode}')" type="radio" name="bankId" value="${commonPay.channelCode }"  class="sm" data-flag="SY"/>
									<img src="${commonPay.logo}"/>
								</label>
							</li>
						</c:forEach>
					</ul>
					<div class="QR-Code-WeChat">
						<img id="syImg" src="${pageContext.request.contextPath}/static/img/loading.gif">
					</div>
					<div class="QR-Code-Ali">
						<iframe id="if1" frameborder="0" height="300" width="600" src="${pageContext.request.contextPath}/static/img/loading.gif"></iframe>
					</div>
					<div class="QR-fail">
						<div class="QR-content">
							<p></p>
							<p>请勿重复提交</p>
							<br>
							<a href="#">查看订单</a>
						</div>
					</div>
				</div>
				</c:if>
				<c:if test="${fn:length(commonPayList) != 0}">
				<div class="dotb">
					<h3>银行卡支付</h3>
					<ul>
						<c:forEach var="commonPay" items="${commonPayList}">
						<li>
							<label>
								<input type="radio" name="bankId" value="${commonPay.channelCode }"  class="sm" onclick="changeChannelType(1)" data-flag="BANK"/>
								<img src="${commonPay.logo}"/>
							</label>
						</li>
					</c:forEach>
					</ul>
					<c:if test="${commonPayList== null || fn:length(commonPayList) > 4}">
					<a href="javascript:void(0)" class="bankDown">更多</a>
					<a href="javascript:void(0)" class="bankUp">收起</a>
					</c:if>
				</div>
				</c:if>
				<c:if test="${fn:length(b2bList) != 0}">
				<div class="pay-bank">
					<h3>企业网银</h3>
					<ul>
						<c:forEach var="b2bPay" items="${b2bList}">
						<li>
							<label>
								<input type="radio" name="bankId" value="${b2bPay.channelCode }"  class="sm" onclick="changeChannelType(4)" data-flag="BANKB2B"/>
								<img src="${b2bPay.logo}"/>
							</label>
						</li>
					</c:forEach>
					</ul>
					<c:if test="${commonPayList== null || fn:length(commonPayList) > 4}">
					<a href="javascript:void(0)" class="bankDown">更多</a>
					<a href="javascript:void(0)" class="bankUp">收起</a>
					</c:if>
				</div>
				</c:if>
			</dd>
		</dl>
		<div class="clearfix">
			<a href="" class="paySubmit rg" target="blank" data-target="paySubmit"><span>确认付款</span></a>
		</div>
		<dl class="question-list">
			<dd class="question-title">支付问题</dd>
			<dd class="question-item">
				<div class="question-q">1. 进行网上在线支付前，银行卡需要办理开通网上银行手续吗？</div>
				<div class="question-a">普通银行卡的“网银支付”，需要事先开通网上银行（具体程序请您咨询相关银行）。</div>
			</dd>
			<dd class="question-item">
				<div class="question-q">2. 银行支付有限额要求吗？</div>
				<div class="question-a">为了保证在线支付的安全性，银行会根据各个商户、消费者情况的不一致进行支付金额限制，由于各银行的限额标准并不完全一致，并且会随国家政策进行调整，请您在办理网上银行时，问明限额标准，或者拨打银行热线电话进行咨询。</div>
			</dd>
			<dd class="question-item">
				<div class="question-q">3. 没有开通网上银行怎么购物？</div>
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
<!-- 支付弹窗 -->
<div class="modal hide to-pay-tip" id="toPayTip" data-backdrop="static">
	<div class="modal-body">
    <div class="modal-header clearfix">
		<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span >×</span></button>
    </div>
    <div class="modal-content">
		<div class="pay-content clearfix">
		<p >付款完成前请不要关闭此窗口。完成付款后请根据您的情况点击下面的按钮。</p>
			<div class="success le">
				<a href="${appUrl}" class="btn rg" ><span>已经完成付款</span></a>
            </div>
			<div class="fail le">
				<a href="" class="btn le def"  data-dismiss="modal"><span>更换支付方式</span></a>
            </div>
			<p align="center">温馨提示：如果是支付限额问题，请选择其他支付方式。</p>
		</div>
    </div>
</div>
</form>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/jq.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/index.js"></script>
</body>
</html>