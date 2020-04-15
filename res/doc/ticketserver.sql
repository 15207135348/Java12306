CREATE DATABASE IF NOT EXISTS `ticketserver` DEFAULT CHARACTER SET utf8;

USE `ticketserver`;

-- 日志表
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`
(
    `id`      int(20)   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user`    varchar(50)    DEFAULT NULL COMMENT '请求人',
    `method`  varchar(255)   DEFAULT NULL COMMENT '方法名',
    `start`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '开始时间',
    `end`     timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '结束时间',
    `success` SMALLINT(0)    DEFAULT NULL COMMENT '是否成功',
    `caused`  TEXT           DEFAULT NULL COMMENT '失败原因',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;


-- 用户表
DROP TABLE IF EXISTS `wx_account`;
CREATE TABLE `wx_account`
(
    `id`          int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `open_id`     varchar(50) NOT NULL COMMENT '微信用户ID',
    `session_key` varchar(50) DEFAULT NULL COMMENT '密钥',
    `union_id`    varchar(50) DEFAULT NULL COMMENT '付款用',
    `username` varchar(50) DEFAULT NULL COMMENT '绑定的12306账号',
    `password` varchar(50) DEFAULT NULL COMMENT '绑定的12306账号的密码',
    `priority` smallint (2) DEFAULT 0 COMMENT '用户的优先级',
    PRIMARY KEY (`id`),
    UNIQUE KEY `open_id` (`open_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `user_order`;
CREATE TABLE `user_order`
(
    `id`    int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `order_id` varchar(50) DEFAULT NULL COMMENT 'order_id',
    `from_station` varchar(50) DEFAULT NULL COMMENT 'from_station',
    `to_station` varchar(50) DEFAULT NULL COMMENT 'to_station',
    `dates` varchar(50) DEFAULT NULL COMMENT 'dates',
    `trains` varchar(50) DEFAULT NULL COMMENT 'trains',
    `seats` varchar(50) DEFAULT NULL COMMENT 'seats',
    `people` varchar(50) DEFAULT NULL COMMENT 'people',
    `contact_info` varchar(50) DEFAULT NULL COMMENT '联系方式',
    `rush_type` varchar(50) NOT NULL COMMENT '抢票类型',
    `priority` smallint (2) DEFAULT 0 COMMENT 'priority',
    `order_time` datetime DEFAULT NULL COMMENT '下单时间',
    `expire_time` datetime DEFAULT NULL COMMENT '订单过期时间',
    `status` varchar(50) DEFAULT NULL COMMENT 'status',
    `query_count` int(20) DEFAULT 0 COMMENT 'query_count',
    `open_id` varchar(50) DEFAULT NULL COMMENT 'open_id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `order_id` (`order_id`),
    KEY `open_id` (`open_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `train_order`;
CREATE TABLE `train_order`
(
    `id`    int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `sequence_no` varchar(50) DEFAULT NULL COMMENT 'sequence_no',
    `order_time` datetime DEFAULT NULL COMMENT '下单时间',
    `train_code` varchar(50) DEFAULT NULL COMMENT '车次',
    `from_station` varchar(50) DEFAULT NULL COMMENT 'from_station',
    `to_station` varchar(50) DEFAULT NULL COMMENT 'to_station',
    `from_time` datetime DEFAULT NULL COMMENT '开车时间',
    `to_time` datetime DEFAULT NULL COMMENT '到站时间',
    `duration` varchar(50) DEFAULT NULL COMMENT 'duration',
    `status` varchar(50) DEFAULT NULL COMMENT 'status',
    `user_order_id` varchar(50) DEFAULT NULL COMMENT 'user_order_id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `sequence_no` (`sequence_no`),
    UNIQUE KEY `user_order_id` (`user_order_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `train_ticket`;
CREATE TABLE `train_ticket`
(
    `id`    int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `ticket_no` varchar(50) DEFAULT NULL COMMENT 'ticket_no',
    `ticket_type` varchar(50) DEFAULT NULL COMMENT 'ticket_type',
    `passenger_name` varchar(50) DEFAULT NULL COMMENT 'passenger_name',
    `passenger_id` varchar(50) DEFAULT NULL COMMENT 'passenger_id',
    `passenger_id_type` varchar(50) DEFAULT NULL COMMENT 'passenger_id_type',
    `coach` varchar (50) DEFAULT NULL COMMENT 'coach',
    `seat` varchar (50) DEFAULT NULL COMMENT 'seat',
    `seat_type` varchar (50) DEFAULT NULL COMMENT 'seat_type',
    `price` varchar(50) DEFAULT NULL COMMENT 'price',
    `user_order_id` varchar(50) DEFAULT NULL COMMENT 'user_order_id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ticket_no` (`ticket_no`),
    KEY `user_order_id` (`user_order_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `train_an_order`;
CREATE TABLE `train_an_order`
(
    `id`    int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `reserve_no` varchar(50) DEFAULT NULL COMMENT 'reserve_no',
    `order_time` datetime DEFAULT NULL COMMENT '下单时间',
    `train_code` varchar(50) DEFAULT NULL COMMENT '车次',
    `from_station` varchar(50) DEFAULT NULL COMMENT 'from_station',
    `to_station` varchar(50) DEFAULT NULL COMMENT 'to_station',
    `from_time` datetime DEFAULT NULL COMMENT '开车时间',
    `to_time` datetime DEFAULT NULL COMMENT '到站时间',
    `duration` varchar(50) DEFAULT NULL COMMENT 'duration',
    `seat_type` varchar(50) DEFAULT NULL COMMENT 'seat_type',
    `queue_info` varchar(50) DEFAULT NULL COMMENT '候补人数',
    `prepay_amount` varchar(50) DEFAULT NULL COMMENT '需要支付的金额',
    `status` varchar(50) DEFAULT NULL COMMENT 'status',
    `passengers` varchar(255) DEFAULT NULL COMMENT '乘客信息',
    `user_order_id` varchar(50) DEFAULT NULL COMMENT 'user_order_id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `reserve_no` (`reserve_no`),
    UNIQUE KEY `user_order_id` (`user_order_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;


-- 乘客
DROP TABLE IF EXISTS `passenger`;
CREATE TABLE `passenger`
(
    `id`          int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        varchar(50) DEFAULT NULL COMMENT '乘客名',
    `type`        varchar(50) DEFAULT NULL COMMENT '乘客类型',
    `id_no`       varchar(50) DEFAULT NULL COMMENT '证件号',
    `id_type`     varchar(50) DEFAULT NULL COMMENT '证件类型',
    `username`    varchar(50) DEFAULT NULL COMMENT '12306账号',
    `if_receive`  smallint DEFAULT NULL COMMENT '手机是否核验',
    `mobile`      varchar(50) DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username_name` (`username`,`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;



DROP TABLE IF EXISTS `train_ticket_price`;
CREATE TABLE `train_ticket_price`
(
    `id`    int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `train_code` varchar(50) DEFAULT NULL COMMENT 'train_code',
    `from_station` varchar(50) DEFAULT NULL COMMENT 'from_station',
    `to_station` varchar(50) DEFAULT NULL COMMENT 'to_station',
    `lowest_price` varchar(50) DEFAULT NULL COMMENT 'lowest_price',
    `prices` varchar(255) DEFAULT NULL COMMENT 'prices',
    PRIMARY KEY (`id`),
    UNIQUE KEY `train_code_from_to` (`train_code`,`from_station`,`to_station`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS `http_proxy`;
CREATE TABLE `http_proxy`
(
    `id`            int(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `ip`            varchar(50) NOT NULL COMMENT 'ip地址',
    `port`          int(5) NOT NULL COMMENT '端口地址',
    `is_https`      smallint NOT NULL COMMENT '是否为https',
    `is_anonymous`  smallint NOT NULL COMMENT '是否高匿',
    `speed`         float DEFAULT NULL COMMENT '速度',
    `conn_time`     float DEFAULT NULL COMMENT '连接时间',
    `check_time`    datetime DEFAULT NULL COMMENT '上次检测可用的时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ip_port` (`ip`,`port`),
    KEY `check_time`(`check_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;