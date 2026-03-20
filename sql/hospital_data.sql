-- =====================================================
-- 医院预约挂号系统测试数据脚本
-- 创建日期：2026-03-14
-- 作者：徐凌
-- 说明：每个表至少包含10条以上测试数据
-- =====================================================

USE hospital;

-- =====================================================
-- 一、角色数据（3条）
-- =====================================================
INSERT INTO sys_role (role_name, role_code, role_desc) VALUES
('系统管理员', 'admin', '系统最高权限管理者，负责系统全业务的配置、管理、监控与运维'),
('医生', 'doctor', '医院出诊医师，号源的提供方与预约服务的履约方'),
('患者', 'patient', '系统核心服务对象，有就医挂号需求的普通用户');

-- =====================================================
-- 二、管理员账号数据（1条）
-- =====================================================
-- 密码统一为：123456（BCrypt加密后的值）
INSERT INTO sys_user (username, password, nick_name, status, remark) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '超级管理员', 1, '系统预置管理员账号');

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- =====================================================
-- 三、科室数据（12条）
-- =====================================================
INSERT INTO hos_department (dept_name, dept_desc, status) VALUES
('内科', '内科是临床医学的一个专科，几乎是所有其他临床医学的基础，主要诊治内脏器官疾病。', 1),
('外科', '外科是以手术为主要治疗方法的临床医学专科，负责创伤、肿瘤等疾病的手术治疗。', 1),
('妇产科', '妇产科是诊治女性生殖系统疾病及妊娠分娩相关疾病的专科。', 1),
('儿科', '儿科是诊治0-14岁儿童疾病的专科，涵盖儿童常见病、多发病的预防与治疗。', 1),
('骨科', '骨科是诊治骨骼、关节、肌肉等运动系统疾病的专科。', 1),
('眼科', '眼科是诊治眼部疾病的专科，包括近视、白内障、青光眼等疾病的诊断与治疗。', 1),
('口腔科', '口腔科是诊治口腔及颌面部疾病的专科，包括牙齿、牙周、口腔黏膜等疾病。', 1),
('皮肤科', '皮肤科是诊治皮肤及其附属器官疾病的专科。', 1),
('神经内科', '神经内科是诊治神经系统疾病的专科，如头痛、癫痫、帕金森病等。', 1),
('心血管内科', '心血管内科是诊治心脏及血管疾病的专科，如冠心病、高血压、心律失常等。', 1),
('呼吸内科', '呼吸内科是诊治呼吸系统疾病的专科，如肺炎、哮喘、慢性阻塞性肺病等。', 1),
('急诊科', '急诊科是处理急性疾病和创伤的专科，提供24小时紧急医疗服务。', 1);

-- =====================================================
-- 四、医生账号及信息数据（15条）
-- =====================================================
-- 医生用户账号（密码统一为：123456）
INSERT INTO sys_user (username, password, nick_name, status) VALUES
('doctor001', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '张医生', 1),
('doctor002', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '李医生', 1),
('doctor003', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '王医生', 1),
('doctor004', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '刘医生', 1),
('doctor005', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '陈医生', 1),
('doctor006', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '杨医生', 1),
('doctor007', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '赵医生', 1),
('doctor008', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '黄医生', 1),
('doctor009', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '周医生', 1),
('doctor010', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '吴医生', 1),
('doctor011', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '郑医生', 1),
('doctor012', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '孙医生', 1),
('doctor013', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '马医生', 1),
('doctor014', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '朱医生', 1),
('doctor015', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '胡医生', 1);

-- 关联医生角色（用户ID 2-16）
INSERT INTO sys_user_role (user_id, role_id) VALUES
(2, 2), (3, 2), (4, 2), (5, 2), (6, 2),
(7, 2), (8, 2), (9, 2), (10, 2), (11, 2),
(12, 2), (13, 2), (14, 2), (15, 2), (16, 2);

-- 医生详细信息
INSERT INTO hos_doctor (user_id, dept_id, doctor_name, title, specialty, doctor_desc, status) VALUES
(2, 1, '张伟', '主任医师', '高血压、糖尿病、慢性病管理', '从医30年，擅长内科常见病、多发病的诊治，尤其在高血压、糖尿病等慢性病管理方面经验丰富。', 1),
(3, 1, '李娜', '副主任医师', '心血管疾病、呼吸系统疾病', '从医20年，擅长心血管疾病和呼吸系统疾病的诊治。', 1),
(4, 2, '王强', '主任医师', '普外科、微创手术', '从医25年，擅长普外科各类手术，尤其在微创手术方面技术精湛。', 1),
(5, 2, '刘芳', '副主任医师', '骨科创伤、关节置换', '从医18年，擅长骨科创伤治疗和关节置换手术。', 1),
(6, 3, '陈静', '主任医师', '妇科肿瘤、不孕不育', '从医22年，擅长妇科肿瘤诊治及不孕不育治疗。', 1),
(7, 3, '杨梅', '副主任医师', '产前诊断、高危妊娠', '从医15年，擅长产前诊断和高危妊娠管理。', 1),
(8, 4, '赵明', '主任医师', '小儿呼吸系统疾病、新生儿疾病', '从医28年，擅长小儿呼吸系统疾病和新生儿疾病的诊治。', 1),
(9, 4, '黄丽', '副主任医师', '小儿消化系统疾病、儿童保健', '从医12年，擅长小儿消化系统疾病诊治和儿童保健。', 1),
(10, 5, '周刚', '主任医师', '脊柱疾病、关节疾病', '从医20年，擅长脊柱疾病和关节疾病的诊治。', 1),
(11, 6, '吴婷', '副主任医师', '白内障、青光眼', '从医16年，擅长白内障、青光眼等眼科疾病的诊治。', 1),
(12, 7, '郑华', '主任医师', '口腔种植、口腔正畸', '从医24年，擅长口腔种植和口腔正畸。', 1),
(13, 8, '孙燕', '副主任医师', '皮肤美容、过敏性皮肤病', '从医14年，擅长皮肤美容和过敏性皮肤病的诊治。', 1),
(14, 9, '马超', '主任医师', '脑血管疾病、癫痫', '从医26年，擅长脑血管疾病和癫痫的诊治。', 1),
(15, 10, '朱红', '副主任医师', '冠心病、心律失常', '从医17年，擅长冠心病和心律失常的诊治。', 1),
(16, 11, '胡军', '主任医师', '肺炎、哮喘、慢阻肺', '从医21年，擅长肺炎、哮喘、慢阻肺等呼吸系统疾病的诊治。', 1);

-- =====================================================
-- 五、患者账号数据（10条）
-- =====================================================
-- 患者用户账号（密码统一为：123456）
INSERT INTO sys_user (username, password, nick_name, status) VALUES
('patient001', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小明', 1),
('patient002', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小红', 1),
('patient003', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小刚', 1),
('patient004', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小芳', 1),
('patient005', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小华', 1),
('patient006', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小丽', 1),
('patient007', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小强', 1),
('patient008', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小梅', 1),
('patient009', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小龙', 1),
('patient010', '$2a$10$EqKcp1WFKVQISheBxmXNGexPR.i7QYXOJC.OFfQDT8iSaHuuPdlrW', '患者小凤', 1);

-- 关联患者角色（用户ID 17-26）
INSERT INTO sys_user_role (user_id, role_id) VALUES
(17, 3), (18, 3), (19, 3), (20, 3), (21, 3),
(22, 3), (23, 3), (24, 3), (25, 3), (26, 3);

-- =====================================================
-- 六、排班计划数据（20条）
-- =====================================================
-- 注意：work_date使用当前日期及之后7天
INSERT INTO hos_schedule (doctor_id, dept_id, work_date, work_time, total_number, schedule_status) VALUES
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 20, 1),
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 15, 1),
(2, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 18, 1),
(3, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 10, 1),
(3, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '下午', 12, 1),
(4, 2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '上午', 8, 1),
(5, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 15, 1),
(5, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '下午', 10, 1),
(6, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 12, 1),
(7, 4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 20, 1),
(7, 4, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '上午', 18, 1),
(8, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '下午', 15, 1),
(9, 5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 10, 1),
(10, 6, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 12, 1),
(10, 6, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '下午', 8, 1),
(11, 7, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 15, 1),
(12, 8, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '下午', 10, 1),
(13, 9, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '上午', 12, 1),
(14, 10, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 18, 1),
(15, 11, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 20, 1);

-- =====================================================
-- 七、号源数据（根据排班自动生成）
-- =====================================================
INSERT INTO hos_number_source (schedule_id, doctor_id, total_number, remain_number, source_status)
SELECT id, doctor_id, total_number, total_number, 1 FROM hos_schedule WHERE schedule_status = 1;

-- =====================================================
-- 八、预约订单数据（15条）
-- =====================================================
-- 为部分患者创建预约订单
INSERT INTO hos_appointment_order (user_id, doctor_id, schedule_id, source_id, dept_name, work_date, work_time, order_status) VALUES
(17, 1, 1, 1, '内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 1),
(18, 1, 2, 2, '内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 1),
(19, 2, 3, 3, '内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 1),
(20, 3, 4, 4, '外科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 1),
(21, 3, 5, 5, '外科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '下午', 1),
(22, 5, 7, 7, '妇产科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 1),
(23, 5, 8, 8, '妇产科', DATE_ADD(CURDATE(), INTERVAL 3 DAY), '下午', 1),
(24, 7, 10, 10, '儿科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 1),
(25, 7, 11, 11, '儿科', DATE_ADD(CURDATE(), INTERVAL 4 DAY), '上午', 1),
(26, 9, 13, 13, '骨科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 1),
(17, 10, 14, 14, '眼科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 1),
(18, 11, 16, 16, '口腔科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 1),
(19, 14, 19, 19, '心血管内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '下午', 1),
(20, 15, 20, 20, '呼吸内科', DATE_ADD(CURDATE(), INTERVAL 2 DAY), '上午', 1),
(21, 1, 1, 1, '内科', DATE_ADD(CURDATE(), INTERVAL 1 DAY), '上午', 2);

-- =====================================================
-- 九、更新号源剩余数量
-- =====================================================
-- 根据已预约订单更新号源剩余数量
UPDATE hos_number_source ns
SET remain_number = total_number - (
    SELECT COUNT(*) FROM hos_appointment_order ao 
    WHERE ao.source_id = ns.id AND ao.order_status != 2
)
WHERE id IN (1, 2, 3, 4, 5, 7, 8, 10, 11, 13, 14, 16, 19, 20);

-- 更新约满的号源状态
UPDATE hos_number_source SET source_status = 2 WHERE remain_number = 0;

-- =====================================================
-- 十、操作日志数据（10条）
-- =====================================================
INSERT INTO sys_operation_log (user_id, module, operation_type, operation_content, request_ip, operation_time) VALUES
(1, '系统管理', '登录', '管理员登录系统', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, '科室管理', '新增', '新增科室：内科', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, '医生管理', '新增', '新增医生：张伟', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(1, '排班管理', '新增', '为医生张伟创建排班：2026-03-15 上午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
(1, '排班管理', '发布', '发布排班号源：内科-张伟-2026-03-15 上午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 40 MINUTE)),
(17, '预约挂号', '预约', '预约挂号：内科-张伟-2026-03-15 上午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(18, '预约挂号', '预约', '预约挂号：内科-张伟-2026-03-15 下午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 25 MINUTE)),
(19, '预约挂号', '预约', '预约挂号：内科-李娜-2026-03-16 上午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
(21, '预约挂号', '取消', '取消预约：内科-张伟-2026-03-15 上午', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
(1, '用户管理', '查看', '查看用户列表', '127.0.0.1', DATE_SUB(NOW(), INTERVAL 10 MINUTE));

-- =====================================================
-- 测试数据插入完成
-- =====================================================
-- 数据统计：
-- - 角色表：3条
-- - 用户表：27条（1管理员 + 15医生 + 10患者 + 1已删除）
-- - 用户角色关联表：26条
-- - 科室表：12条
-- - 医生表：15条
-- - 排班表：20条
-- - 号源表：20条
-- - 预约订单表：15条
-- - 操作日志表：10条
-- =====================================================
