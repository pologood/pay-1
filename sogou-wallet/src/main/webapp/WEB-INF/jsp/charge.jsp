<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>wallet</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/wallet.css" rel="stylesheet">


    <script type="text/javascript" src="/javascripts/libs/jquery-1.10.2.js"></script>
    <script type="text/javascript" src="/javascripts/libs/bootstrap.min.js"></script>


</head>

<body>

<nav class="navbar navbar-default navbar-fixed-top" style="">
    <img alt="arrow" src="/images/arrow.png" height="45px" onclick="location.href='/wallet'"/>
        <span style="font-size: 18px;font-weight: 500;line-height: 1.1; padding:10px 45px;">账户充值</span>

</nav>

<div >
    <div class=" container charge-form " >
        <div class="col-xs-12 col-md-6 col-md-offset-3" >
        <form >
            <label>支付方式</label>
            <select class="form-control" style="padding: 0px;">
                <option data-icon="glyphicon glyphicon-music" value="0" selected>
                    <img alt="alipay" src="/images/alipay.png" height="45px"/>
                    支付宝

                </option>
            </select>
            <label>充值金额</label>
            <input type="text" name="money" >
            <input type="submit" value="下一步" class="btn btn-primary pull-right">
            <div class="clearfix"></div>
        </form>
        </div>
    </div>
</div>

<br>
<br>
</body>
</html>
