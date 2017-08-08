//存放主要的交互逻辑js代码 js也可以模块化
var seckill = {
    //封装秒杀相关Ajax的url
    url : {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution : function (seckillId, md5) {
            return '/seckill/' + seckillId + "/" + md5 + '/execute';
        }
    },
    //验证手机号
    validatePhone : function (userPhone) {
        if(userPhone && userPhone.length == 11 && !isNaN(userPhone)) {
            return true;
        } else {
            return false;
        }
    },
    handleSeckill : function (seckillId, node) {
        //处理秒杀逻辑，获取秒杀地址，执行秒杀操作
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.url.exposer(seckillId), {}, function (result) {
            if(result && result['success']) {
                var exposer = result['data'];
                if(exposer['exposed']) {
                    //开始秒杀
                    // 获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.url.execution(seckillId, md5);
                    console.log('killUrl : ' + killUrl);
                    //为按钮注册秒杀事件
                    //为按钮只绑定一次事件，防止用户一直点击秒杀按钮 后端同时传入大量的url请求
                    $('#killBtn').one('click', function () {
                        //绑定执行秒杀操作
                        //1.先禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if(result && result['success']) {
                                var seckillExecution = result['data'];
                                var state = seckillExecution['state'];
                                var stateInfo = seckillExecution['stateInfo'];
                                //3.显示秒杀结果
                                node.html('<span class="label label-success">'+ stateInfo + '</span>');
                            }
                        });
                    });
                node.show();
                } else {
                    //未开始秒杀
                    var now = result['now'];
                    var start = result['start'];
                    var end = result['end'];
                    //重新进入计时逻辑
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.log();
            }
        })
    },
    countDown : function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        //seckillBox.html('验证是否成功调用');
        //时间判断
        if(nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if(nowTime < startTime) {
            //秒杀未开始，开始计时
            //加1秒 防止用户时间偏移
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                /**
                 * 时间完成后回调事件
                 */
            }).on('finish.countdown', function () {
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始
            seckillBox.html('开始秒杀');
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail : {
        //详情页初始化
        init : function (params) {
            /**
             * 1.用户手机验证,在cookie中查找手机号
             * 2.倒计时
             */
            var userPhone = $.cookie('userPhone');
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            if(!seckill.validatePhone(userPhone)) {
                //先绑定手机号
                var userPhoneModal = $('#userPhoneModal');
                userPhoneModal.modal({
                    show : true, //显示弹出层
                    backdrop : 'static', //禁止位置事件关闭
                    keyboard : false //关闭键盘事件
                });
                //对button做事件绑定
                $('#userPhoneBtn').click(function () {
                    var userPhone = $('#userPhone').val();
                    if(seckill.validatePhone(userPhone)) {
                        //验证通过,电话写入cookie, 只在seckill页面有效
                        $.cookie('userPhone', userPhone, {express : 7, path : '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#userPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
            //已经登陆，开始做计时交互
            $.get(seckill.url.now(), {}, function (result) {
                if(result && result['success']) {
                    var nowTime = result['data'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    //将日志输出到浏览器控制台
                    console.log('result : ' + result);
                }
            });
        }
    }
}