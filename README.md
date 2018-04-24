# seckill
使用Spring+SpringMVC+Mybatis搭建的简单秒杀系统，仅供学习使用
部署步骤：
一 数据库
1、创建数据库和表，初始化数据
(1)登录mysql -u用户名 -p密码 -h127.0.0.1
(2)拷贝schemal.sql里的SQL就可以
(3)提示有错误，可以百度搜一下  windows 修改  global.sql_mode
(4)修改后，重新执行sql
2、掌握常用的SQL命令。比较查看数据库，查看表，查看表的数据。百度就有
3、执行proc_seckill.sql文件，直接copy到终端即可
二 项目导入IDEA
1、可以参考这个：https://my.oschina.net/alexnine/blog/737698
2、需要下载maven（研发常用的项目构建工具）
三 Tomcat
1、前提是下载好Tomcat，建议下载Tomcat 8.5...
2、工程部署到Tomcat可以参考这个：https://www.jianshu.com/p/c5a0c3e9fa75

