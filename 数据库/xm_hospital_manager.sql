/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : xm_hospital_manager

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 01/12/2023 10:07:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', '管理员', 'http://localhost:9090/files/1697438073596-avatar.png', 'ADMIN', '13677889922', 'admin@xm.com');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '科室描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '科室信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '内科', '这是内科');
INSERT INTO `department` VALUES (2, '外科', '这是外科');
INSERT INTO `department` VALUES (3, '皮肤科', '这是皮肤科');
INSERT INTO `department` VALUES (4, '儿科', '这是儿科');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '简介',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '挂号费',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入职时间',
  `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职位',
  `department_id` int(10) NULL DEFAULT NULL COMMENT '科室ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '医生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1, 'zhang', '123456', '张天志', 'http://localhost:9090/files/1701163828006-柴犬.jpeg', 'DOCTOR', '18800009999', 'zhang@xm.com', '该项目在知识星球【项目训练营】内提供，可以在B站介绍该项目的介绍视频里，有二维码，也可以下载知识星球app，搜索：项目训练营。', 20.00, '2023-11-01', '主治医生', 4);
INSERT INTO `doctor` VALUES (2, 'zhao', '123456', '赵千里', 'http://localhost:9090/files/1701163828006-柴犬.jpeg', 'DOCTOR', '18877776666', 'zhao@xm.com', '该项目在知识星球【项目训练营】内提供，可以在B站介绍该项目的介绍视频里，有二维码，也可以下载知识星球app，搜索：项目训练营。', 20.00, '2023-11-07', '副主治医师', 4);
INSERT INTO `doctor` VALUES (3, 'qian', '123456', '钱有有', 'http://localhost:9090/files/1701163828006-柴犬.jpeg', 'DOCTOR', '18811112222', 'qian@xm.com', '该项目在知识星球【项目训练营】内提供，可以在B站介绍该项目的介绍视频里，有二维码，也可以下载知识星球app，搜索：项目训练营。', 20.00, '2023-11-29', '主治医师', 4);
INSERT INTO `doctor` VALUES (4, 'wang', '123456', '王有为', 'http://localhost:9090/files/1701163828006-柴犬.jpeg', 'DOCTOR', '18800009999', 'wang@xm.com', '该项目在知识星球【项目训练营】内提供，可以在B站介绍该项目的介绍视频里，有二维码，也可以下载知识星球app，搜索：项目训练营。', 20.00, '2023-11-29', '副主任医师', 4);
INSERT INTO `doctor` VALUES (5, 'sun', '123456', '孙晓红', 'http://localhost:9090/files/1701163828006-柴犬.jpeg', 'DOCTOR', '18899997676', 'sun@xm.com', '该项目在知识星球【项目训练营】内提供，可以在B站介绍该项目的介绍视频里，有二维码，也可以下载知识星球app，搜索：项目训练营。', 20.00, '2023-11-29', '主治医师', 3);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建时间',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '今天系统正式上线，开始内测', '今天系统正式上线，开始内测', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (2, '所有功能都已完成，可以正常使用', '所有功能都已完成，可以正常使用', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (3, '今天天气很不错，可以出去一起玩了', '今天天气很不错，可以出去一起玩了', '2023-09-05', 'admin');

-- ----------------------------
-- Table structure for plan
-- ----------------------------
DROP TABLE IF EXISTS `plan`;
CREATE TABLE `plan`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `doctor_id` int(10) NULL DEFAULT NULL COMMENT '医生ID',
  `num` int(10) NULL DEFAULT NULL COMMENT '就诊数量',
  `week` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '周几',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排班信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of plan
-- ----------------------------
INSERT INTO `plan` VALUES (1, 1, 20, '星期四');
INSERT INTO `plan` VALUES (3, 2, 20, '星期四');
INSERT INTO `plan` VALUES (4, 3, 20, '星期四');
INSERT INTO `plan` VALUES (5, 4, 20, '星期四');
INSERT INTO `plan` VALUES (6, 5, 20, '星期四');

-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE `record`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '患者ID',
  `doctor_id` int(10) NULL DEFAULT NULL COMMENT '医生ID',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '就诊时间',
  `medical_record` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '医嘱病历',
  `inhospital` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否住院',
  `inhostpital_record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否住院登记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '就诊记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of record
-- ----------------------------
INSERT INTO `record` VALUES (3, 1, 1, '2023-11-30', '<p>病历：</p><p>感冒发烧严重，伴有咳嗽现象，<b><font color=\"#c24f4a\">建议留院观察</font></b>。</p><p>节流阀将诶链接发我i垃圾覅了危机爱拉法基啊</p><p>i拉我家覅刘家洼诶阿金发哇i金额非久啊房间诶jawi</p><p>if垃圾啊额立即爱垃圾未jawif较为飞机</p><p><br/></p><p>医嘱：</p><p><b><font color=\"#c24f4a\">禁止饮酒、禁止吃油腻辛辣的食物。</font></b></p>', '是', '是');
INSERT INTO `record` VALUES (4, 1, 5, '2023-11-30', '<p>病历：皮肤过敏严重，大腿内测局部有溃疡现象，<b><font color=\"#c24f4a\">建议留院治疗和观察</font></b>。</p><p>甲方拉近了激发理解诶垃圾发啦忘记诶拉法基</p><p>放假哦i啊我就诶冷风机啊累计额覅拉瓦金额覅垃圾啊为</p><p>发力啊我就诶哦飞机啊我i了激发俩娃金额覅了</p><p>尽快覅哦啊文件额俩肌无力飞机啊额我i</p><p><br/></p><p>医嘱：不要饮酒、不要抽烟、不要吃辛辣刺激性食物。</p>', '是', '是');

-- ----------------------------
-- Table structure for registration
-- ----------------------------
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '患者ID',
  `room` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '房号',
  `price` double(10, 2) NULL DEFAULT NULL COMMENT '费用',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缴费状态',
  `medicine` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否医保',
  `hos_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '住院状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '住院登记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of registration
-- ----------------------------
INSERT INTO `registration` VALUES (1, 1, '001', 3000.00, '已缴费', '是', '住院中');

-- ----------------------------
-- Table structure for reserve
-- ----------------------------
DROP TABLE IF EXISTS `reserve`;
CREATE TABLE `reserve`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(10) NULL DEFAULT NULL COMMENT '患者ID',
  `doctor_id` int(10) NULL DEFAULT NULL COMMENT '医生ID',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '挂号时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '挂号状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '预约挂号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reserve
-- ----------------------------
INSERT INTO `reserve` VALUES (11, 1, 1, '2023-11-30', '已叫号');
INSERT INTO `reserve` VALUES (12, 2, 1, '2023-11-30', '未叫号');
INSERT INTO `reserve` VALUES (13, 2, 4, '2023-11-30', '未叫号');
INSERT INTO `reserve` VALUES (14, 1, 5, '2023-11-30', '已叫号');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `account` double(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '患者信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangsan', '123456', '张三', 'http://localhost:9090/files/1701164522569-柯基.jpeg', 'USER', '18899990000', 'zhangsan@xm.com', 200.00);
INSERT INTO `user` VALUES (2, 'lisi', '123456', '李四', NULL, 'USER', '18866665555', 'lisi@xm.com', 0.00);
INSERT INTO `user` VALUES (3, 'wangwu', '123456', 'wangwu', NULL, 'USER', NULL, NULL, 0.00);

SET FOREIGN_KEY_CHECKS = 1;
