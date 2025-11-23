-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital_appointment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hospital_appointment;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(20) COMMENT '真实姓名',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    id_card VARCHAR(18) COMMENT '身份证号',
    gender VARCHAR(10) COMMENT '性别(male:男,female:女)',
    birthday DATE COMMENT '出生日期',
    address VARCHAR(255) COMMENT '地址',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    description VARCHAR(255) COMMENT '权限描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- 角色权限关系表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';

-- 科室表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '科室ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '科室名称',
    description TEXT COMMENT '科室描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态(0:禁用,1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_name (dept_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- 医生信息表
CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '医生ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    dept_id BIGINT NOT NULL COMMENT '所属科室ID',
    title VARCHAR(50) COMMENT '职称',
    specialty VARCHAR(100) COMMENT '专长领域',
    introduction TEXT COMMENT '医生介绍',
    experience INT COMMENT '工作经验(年)',
    consultation_fee DECIMAL(10,2) DEFAULT 0 COMMENT '挂号费',
    daily_quota INT DEFAULT 20 COMMENT '每日挂号限额',
    status TINYINT DEFAULT 1 COMMENT '状态(0:休假,1:出诊)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES department(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- 预约表
CREATE TABLE IF NOT EXISTS appointment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    appointment_time INTEGER NOT NULL COMMENT '预约时间段(整数表示)',
    status TINYINT DEFAULT 0 COMMENT '预约状态(0:待就诊,1:已就诊,2:已取消,3:已完成,4:已过期)',
    symptoms VARCHAR(255) COMMENT '症状描述',
    diagnosis TEXT COMMENT '诊断结果',
    prescription TEXT COMMENT '处方信息',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 医疗导诊表
CREATE TABLE IF NOT EXISTS medical_guidance (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '导诊ID',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    symptoms_description TEXT NOT NULL COMMENT '症状描述',
    recommended_dept_id BIGINT COMMENT '推荐科室ID',
    guidance_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '导诊时间',
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recommended_dept_id) REFERENCES department(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医疗导诊表';

-- 患者信息扩展表
CREATE TABLE IF NOT EXISTS patient (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '患者ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    age INTEGER COMMENT '年龄',
    birth_date DATE COMMENT '出生日期',
    medical_history TEXT COMMENT '病史',
    allergy_history TEXT COMMENT '过敏史',
    blood_type VARCHAR(10) COMMENT '血型',
    emergency_contact VARCHAR(20) COMMENT '紧急联系人',
    emergency_phone VARCHAR(11) COMMENT '紧急联系电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息扩展表';

-- 确保正确编码
SET NAMES utf8mb4;

-- 初始化角色数据
INSERT INTO role (role_name, role_code, description) VALUES 
('患者', 'ROLE_PATIENT', '普通患者角色'),
('医生', 'ROLE_DOCTOR', '医生角色'),
('管理员', 'ROLE_ADMIN', '系统管理员角色');

-- 初始化权限数据
INSERT INTO permission (permission_name, permission_code, description) VALUES 
('查看科室列表', 'department:list', '查看科室信息列表'),
('查看医生列表', 'doctor:list', '查看医生信息列表'),
('预约挂号', 'appointment:create', '创建预约挂号记录'),
('查看预约记录', 'appointment:list', '查看个人预约记录'),
('取消预约', 'appointment:cancel', '取消未就诊的预约'),
('医疗导诊', 'guidance:create', '提交症状描述获取科室推荐'),
('医生接诊', 'doctor:accept', '医生接诊功能'),
('医生诊断', 'doctor:diagnose', '医生填写诊断结果'),
('管理用户', 'user:manage', '管理系统用户'),
('管理角色', 'role:manage', '管理系统角色'),
('管理科室', 'department:manage', '管理科室信息'),
('管理医生', 'doctor:manage', '管理医生信息'),
('获取所有用户', 'user:list', '获取所有系统用户列表'),
('添加用户', 'user:add', '添加新用户'),
('更新用户', 'user:update', '更新用户信息'),
('删除用户', 'user:delete', '删除用户'),
('更新用户状态', 'user:updateStatus', '更新用户状态'),
('重置密码', 'user:resetPassword', '重置用户密码'),
('批量删除用户', 'user:batchDelete', '批量删除用户'),
('批量启用用户', 'user:batchEnable', '批量启用用户'),
('批量禁用用户', 'user:batchDisable', '批量禁用用户'),
('查看预约详情', 'appointment:detail', '查看预约详情'),
('更新预约状态', 'appointment:updateStatus', '更新预约状态'),
('批量确认预约', 'appointment:batchConfirm', '批量确认预约'),
('批量取消预约', 'appointment:batchCancel', '批量取消预约');

-- 初始化角色权限关系
-- 患者权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6);

-- 医生权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(2, 1), (2, 2), (2, 4), (2, 7), (2, 8);

-- 管理员权限
INSERT INTO role_permission (role_id, permission_id) VALUES 
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 9), (3, 10), (3, 11), (3, 12),
(3, 13), (3, 14), (3, 15), (3, 16), (3, 17), (3, 18), (3, 19), (3, 20), (3, 21),
(3, 22), (3, 23), (3, 24), (3, 25);

-- 初始化管理员用户
INSERT INTO user (username, password, real_name, phone, email, gender, status) VALUES 
('admin', 'admin123', '管理员', '13800138000', 'admin@hospital.com', 'male', 1);

-- 初始化医生用户
INSERT INTO user (username, password, real_name, phone, email, gender, status) VALUES 
('doctor1', 'doctor123', '李医生', '13800138002', 'doctor_li@hospital.com', 'male', 1),
('doctor2', 'doctor123', '王医生', '13800138004', 'doctor_wang@hospital.com', 'female', 1),
('doctor3', 'doctor123', '刘医生', '13800138007', 'doctor_liu@hospital.com', 'male', 1);

-- 初始化患者用户
INSERT INTO user (username, password, real_name, phone, email, gender, status) VALUES 
('patient1', 'patient123', '张三', '13800138001', 'zhangsan@example.com', 'male', 1),
('patient2', 'patient123', '李四', '13800138003', 'lisi@example.com', 'female', 0),
('patient3', 'patient123', '王五', '13800138005', 'wangwu@example.com', 'male', 1),
('patient4', 'patient123', '赵六', '13800138006', 'zhaoliu@example.com', 'female', 1),
('patient5', 'patient123', '孙七', '13800138008', 'sunqi@example.com', 'male', 0),
('patient6', 'patient123', '周八', '13800138009', 'zhouba@example.com', 'female', 1);

-- 初始化科室数据
INSERT INTO department (dept_name, description, sort, status) VALUES
('内科', '诊治呼吸系统、消化系统、心血管系统等疾病', 1, 1),
('外科', '普通外科手术、创伤治疗等', 2, 1),
('妇产科', '妇科疾病诊疗、产科服务', 3, 1),
('儿科', '儿童疾病诊疗和保健', 4, 1),
('眼科', '眼部疾病诊疗', 5, 1),
('耳鼻喉科', '耳鼻喉疾病诊疗', 6, 1);

-- 初始化医生信息
INSERT INTO doctor (user_id, dept_id, title, specialty, introduction, experience, consultation_fee, daily_quota, status) VALUES
(2, 1, '主任医师', '心血管疾病', '从事心血管临床工作20年，擅长冠心病、高血压等疾病诊疗', 20, 50.00, 20, 1),
(3, 2, '副主任医师', '普外科', '擅长腹腔镜手术，对胆囊、阑尾等常见外科疾病有丰富经验', 15, 40.00, 20, 1),
(4, 3, '主治医师', '产科', '专注产科工作10年，对高危妊娠管理有丰富经验', 10, 30.00, 15, 1);

-- 初始化患者扩展信息
INSERT INTO patient (user_id, blood_type, emergency_contact, emergency_phone) VALUES
(5, 'A', '张母', '13800138010'),
(6, 'O', '李父', '13800138011'),
(7, 'B', '王妻', '13800138012'),
(8, 'AB', '赵母', '13800138013'),
(9, 'A', '孙父', '13800138014'),
(10, 'O', '周妻', '13800138015');

-- 关联用户角色
INSERT INTO user_role (user_id, role_id) VALUES 
(1, 3), -- 管理员
(2, 2), (3, 2), (4, 2), -- 医生
(5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1); -- 患者
