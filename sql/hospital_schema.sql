-- =====================================================
-- 医院预约挂号系统数据库建表脚本
-- 数据库名称：hospital
-- 字符集：utf8mb4
-- 排序规则：utf8mb4_general_ci
-- 创建日期：2026-03-14
-- 作者：徐凌
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE hospital;

-- =====================================================
-- 一、系统权限模块表
-- =====================================================

-- 1. 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色唯一ID',
    role_name VARCHAR(32) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(64) NOT NULL COMMENT '角色标识',
    role_desc VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 系统用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID',
    username VARCHAR(64) NOT NULL COMMENT '登录用户名',
    password VARCHAR(128) NOT NULL COMMENT '登录密码（BCrypt加密）',
    nick_name VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-停用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注信息',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 3. 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 系统操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '操作用户ID',
    module VARCHAR(64) NOT NULL COMMENT '操作模块',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    operation_content VARCHAR(512) DEFAULT NULL COMMENT '操作内容',
    request_ip VARCHAR(64) DEFAULT NULL COMMENT '请求IP',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

-- =====================================================
-- 二、医院基础信息模块表
-- =====================================================

-- 5. 科室信息表
DROP TABLE IF EXISTS hos_department;
CREATE TABLE hos_department (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '科室ID',
    dept_name VARCHAR(64) NOT NULL COMMENT '科室名称',
    dept_desc VARCHAR(512) DEFAULT NULL COMMENT '科室简介',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '科室状态：1-启用，0-停用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_name (dept_name),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室信息表';

-- 6. 医生信息表
DROP TABLE IF EXISTS hos_doctor;
CREATE TABLE hos_doctor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '医生ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    dept_id BIGINT NOT NULL COMMENT '所属科室ID',
    doctor_name VARCHAR(32) NOT NULL COMMENT '医生姓名',
    title VARCHAR(32) DEFAULT NULL COMMENT '执业职称',
    specialty VARCHAR(255) DEFAULT NULL COMMENT '专业擅长',
    doctor_desc VARCHAR(512) DEFAULT NULL COMMENT '医生简介',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '医生头像',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-停用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_dept_id (dept_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- =====================================================
-- 三、排班与号源模块表
-- =====================================================

-- 7. 排班计划表
DROP TABLE IF EXISTS hos_schedule;
CREATE TABLE hos_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '排班ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    dept_id BIGINT NOT NULL COMMENT '科室ID',
    work_date DATE NOT NULL COMMENT '出诊日期',
    work_time VARCHAR(32) NOT NULL COMMENT '出诊时段',
    total_number INT NOT NULL DEFAULT 1 COMMENT '号源总量',
    schedule_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '排班状态：0-未发布，1-已发布，2-已下架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_doctor_schedule (doctor_id, work_date, work_time),
    KEY idx_dept_id (dept_id),
    KEY idx_work_date (work_date),
    KEY idx_schedule_status (schedule_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班计划表';

-- 8. 号源信息表
DROP TABLE IF EXISTS hos_number_source;
CREATE TABLE hos_number_source (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '号源ID',
    schedule_id BIGINT NOT NULL COMMENT '排班ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    total_number INT NOT NULL COMMENT '号源总量',
    remain_number INT NOT NULL COMMENT '剩余号源数',
    source_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '号源状态：1-可预约，2-已约满，3-已下架',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_id (schedule_id),
    KEY idx_doctor_id (doctor_id),
    KEY idx_source_status (source_status),
    CONSTRAINT chk_remain_number CHECK (remain_number >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='号源信息表';

-- =====================================================
-- 四、预约订单模块表
-- =====================================================

-- 9. 预约订单表
DROP TABLE IF EXISTS hos_appointment_order;
CREATE TABLE hos_appointment_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '患者用户ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    schedule_id BIGINT NOT NULL COMMENT '排班ID',
    source_id BIGINT NOT NULL COMMENT '号源ID',
    dept_name VARCHAR(64) NOT NULL COMMENT '科室名称',
    work_date DATE NOT NULL COMMENT '预约日期',
    work_time VARCHAR(32) NOT NULL COMMENT '预约时段',
    order_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '订单状态：1-待就诊，2-已取消，3-已完成',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_appointment (user_id, doctor_id, schedule_id),
    KEY idx_doctor_id (doctor_id),
    KEY idx_schedule_id (schedule_id),
    KEY idx_work_date (work_date),
    KEY idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

-- =====================================================
-- 建表完成
-- =====================================================
