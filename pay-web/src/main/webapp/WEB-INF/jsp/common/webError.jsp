<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>搜狗支付中心</title>
<link rel="stylesheet" type="text/css" href="/static/css/base.css"/>
<link rel="stylesheet" type="text/css" href="/static/css/index.css"/>
<style type="text/css">
	*{margin:0;padding:0;}
	ul{list-style: none;}
	body{background: none;}
	img{border: none;}
	a{text-decoration:none;}
	.errorBox{width: 540px; margin: 150px auto;}
	.errorBox:after,.errorBox:before{  content: "";display: table;}
	.errorBox img{float: left;width: 145px;height: 167px;}
	.errorRelate{float: left;width: 395px;}
	.errorTitle{font-size: 20px;color: #000;font-family: "微软雅黑";line-height: 37px;text-align: center;position: relative;}
	.errorDetail{font-size: 25px;color: #000;font-family: "微软雅黑";line-height: 80px;}
	.clearfix {zoom: 1; height: 80px}
	.paySubmit {display: block;height: 36px;padding-left: 10px;margin: 30px auto 55px;color: #fff;text-decoration: none;cursor: pointer;background: url(/static/img/005.png) 0 0 no-repeat;_width: auto;}
	.paySubmit span {display: inline-block;height: 20px;padding: 0 30px 0 20px;font-size: 14px;line-height: 36px;text-align: center;background: url(/static/img/005.png) 100% 0 no-repeat;_width: auto;}
	.wrapper {width: 900px;margin: 0 auto;height: 500px}
</style>

<script>
function closeWindows(){
	if (navigator.userAgent.indexOf("MSIE") > 0) {
		  if (navigator.userAgent.indexOf("MSIE 6.0") > 0) {
		   window.opener = null;
		   window.close();
		  } else {
		   window.open('', '_top');
		   window.top.close();
		  }
		 }
		 else if (navigator.userAgent.indexOf("Firefox") > 0) {
		  window.close();
		 } else {
			 window.opener = null;
			 window.open('','_self');
			 window.close();
		 }   
}
</script>
</head>

<body>
<div class="header">
	
</div>

<div class="wrapper">
	<div class="logo-area clearfix">
		<div class="logo le"></div>
		<div class="logo-title le">搜狗支付中心</div>
	</div>
	<div class="errorBox">
		<img src="/static/img/search.jpg">
		<div class="errorRelate">
			<h2 class="errorTitle"></h2>
			<h2 class="errorDetail">${message}</h2>
		</div>
	</div>
	<div class="clearfix">
			<a  onclick="closeWindows()" class="paySubmit rg" target="blank"><span>关闭</span></a>
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

<script type="text/javascript" src="/static/js/jq.js"></script>
<script type="text/javascript" src="/static/js/index.js"></script>
</body>
</html>