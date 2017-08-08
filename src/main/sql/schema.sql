-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀表
CREATE TABLE seckill(
  seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  number INT NOT NULL COMMENT '库存数量',
  name VARCHAR(120) NOT NULL COMMENT '商品名称',
  start_time TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀开始时间',
  end_time TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY inx_start_time(start_time),
  KEY inx_end_time(end_time),
  KEY inx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
insert into seckill(name,number,start_time,end_time,create_time)
values
  ('1000元秒杀iPhone6',100,'2017-08-06 00:00:00','2017-08-07 00:00:00','2017-08-05 00:00:00'),
  ('500元秒杀ipad',200,'2017-08-07 00:00:00','2017-08-08 00:00:00','2017-08-05 00:00:00'),
  ('300元秒杀小米4',300,'2017-08-08 00:00:00','2017-08-09 00:00:00','2017-08-05 00:00:00'),
  ('200元秒杀红米Note',400,'2017-08-09 00:00:00','2017-08-10 00:00:00','2017-08-05 00:00:00');

-- 秒杀成功表
-- 用户登陆认证相关信息
CREATE TABLE success_killed(
  seckill_id BIGINT NOT NULL COMMENT '商品库存id',
  user_phone BIGINT NOT NULL COMMENT '用户手机号',
  state TINYINT NOT NULL DEFAULT -1 COMMENT '-1 无效 0成功 1已付款 2已发货',
  create_time TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建',
  PRIMARY KEY (seckill_id,user_phone),
  KEY inx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 连接数据库控制台
mysql -u *** -p ***
