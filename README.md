这是一款基于Java开发的12306抢票小程序的后端代码

前端代码传送门

`https://github.com/15207135348/qiangpiaojiang`

12306接口文档传送门

`https://github.com/15207135348/12306InterfaceDoc`

#### 功能列表

*   [x]  车次查询
*   [x]  车次筛选
*   [x]  自动打码
*   [x]  自动登录
*   [x]  普通刷票
*   [x]  候补抢票
*   [x]  微信通知
*   [x]  邮件通知
*   [x]  用户订单管理
*   [x]  先抢票后付款

*   [ ]  预约抢票
*   [ ]  车次推荐
*   [ ]  坐席推荐
*   [ ]  时段推荐
*   [ ]  在线选座
*   [ ]  成功率预估
*   [ ]  先付款安心抢
*   [ ]  免12306账户密码抢票

#### 项目环境

*   [x]  python 3.6 - 3.7.4
*   [x]  java 1.8
*   [x]  MySQL 5.4.X - 5.7.X

#### 依赖库

*   python依赖库 [requirements.txt](https://github.com/15207135348/Java12306/blob/master/src/main/python/requestments.txt)

    *   安装方法：

        *   `pip install -r requirements.txt`

*   Java依赖库 [pom.xml](https://github.com/15207135348/Java12306/blob/master/pom.xml)

    *   安装方法：

        *   使用maven自动下载和安装依赖库

#### 项目使用说明

- 创建数据库表

- 创建方法

  - 登陆到MySQL数据库中，使用如下命令创建数据库表
  - `source ticketserver.sql`

- 服务器启动

  - 修改application-properties文件

    - 设置连接数据库的用户名和密码

    - ```
      spring.datasource.username = ****
      spring.datasource.password = ****
      ```

    - 配置小程序的appid和secret（需要前往微信开放平台创建一个小程序）

    - ```
      app.id=****
      app.secret=****
      ```

    - 设置微信通知的消息模板（需要前往微信开发平台申请消息模板）

    - ```
      # 抢票成功通知模板
      app.template_common_ticket=****
      # 候补抢票成功通知模板
      app.template_alternate_ticket=****
      ```

  - 启动验证码识别模版

    - `python3 app.py `

  - 启动主程序

    - 在IDEA中运行StartApplication
#### 思路图
![image.png](https://upload-images.jianshu.io/upload_images/12652505-9bef41219fa918f2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 项目申明

- 本软件只供学习交流使用，请不要用作商业用途，交流群号
  - 群号：832236668
- 进群看公告
- 在群文件中获取12306接口文档
- 关注公众号【大数据学堂】，点击原创合集->抢票小程序，观看视频教程

![image.png](https://upload-images.jianshu.io/upload_images/12652505-11ac76079ebc425d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 使用【抢票酱小程序】体验最终效果

![image.png](https://upload-images.jianshu.io/upload_images/12652505-2339921f5266fc60.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
