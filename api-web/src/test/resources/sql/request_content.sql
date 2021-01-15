/*
 Navicat Premium Data Transfer

 Source Server         : 本地环境
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : api_test

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 15/01/2021 18:00:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for request_content
-- ----------------------------
DROP TABLE IF EXISTS `request_content`;
CREATE TABLE `request_content`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '接口的名字',
    `method`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '请求的方法',
    `start_time`     datetime                                                       DEFAULT NULL COMMENT '请求开始时间',
    `end_time`       datetime                                                       DEFAULT NULL COMMENT '请求结束时间',
    `headers`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '请求头部',
    `content`        varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求的内容',
    `request_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '请求状态 OK:成功；FAIL: 失败',
    `url`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接口地址',
    `exception_msg`  varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '异常信息',
    `request_group`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '请求分组名字',
    `thread_name`    varchar(50) COLLATE utf8mb4_general_ci                         DEFAULT NULL COMMENT '当前请求的线程名字',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

SET
FOREIGN_KEY_CHECKS = 1;
