/*
Navicat MySQL Data Transfer

Source Server         : yugi
Source Server Version : 50640
Source Host           : localhost:3306
Source Database       : api_test

Target Server Type    : MYSQL
Target Server Version : 50640
File Encoding         : 65001

Date: 2019-11-04 22:56:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for request_content
-- ----------------------------
DROP TABLE IF EXISTS `request_content`;
CREATE TABLE `request_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `method` varchar(50) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `headers` varchar(255) DEFAULT NULL,
  `content` varchar(2000) DEFAULT NULL,
  `request_status` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `exception_msg` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
