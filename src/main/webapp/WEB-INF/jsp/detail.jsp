<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀商品详情</title>
    <%@ include file="common/head.jsp"%>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h2>${seckill.name}</h2>
        </div>
        <div class="panel-body">
            <h2 class="text-danget">
                <!-- 显示time图标 -->
                <span class="glyphicon glyphicon-time"></span>
                <!-- 展示倒计时 -->
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
        </div>
    </div>
</div>
<div id="userPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-headler">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="userPhone" id="userPhone" placeholder="填手机号^o^" class="form-control" />
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <!-- 验证信息 -->
                <span id="userPhoneMessage" class="glyphicon"></span>
                <button type="button" id="userPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>
                    Submit
                </button>
            </div>
        </div>
    </div>
</div>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!-- jquery cookies操作插件 -->
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- jquery倒计时countdown插件 -->
<script src="http://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<script src="/resource/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
       seckill.detail.init({
          seckillId : ${seckill.seckillId},
          startTime : ${seckill.startTime.time},
          endTime : ${seckill.endTime.time}
       });
    });
</script>
</body>
</html>