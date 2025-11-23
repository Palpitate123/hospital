package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Department;
import com.hospital.entity.MedicalGuidance;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.MedicalGuidanceMapper;
import com.hospital.service.MedicalGuidanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 医疗导诊服务实现类
 * 负责处理导诊记录的创建、查询、科室推荐及状态管理等功能
 */
@Service
public class MedicalGuidanceServiceImpl extends ServiceImpl<MedicalGuidanceMapper, MedicalGuidance> implements MedicalGuidanceService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalGuidanceServiceImpl.class);

    @Autowired
    private MedicalGuidanceMapper medicalGuidanceMapper;
    
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 根据患者ID查询导诊记录列表
     * @param patientId 患者ID
     * @return 导诊记录列表
     */
    @Override
    public List<MedicalGuidance> getGuidanceByPatientId(Long patientId) {
        logger.info("查询患者ID: {} 的导诊记录", patientId);
        return medicalGuidanceMapper.selectByPatientId(patientId);
    }

    /**
     * 创建导诊记录
     * @param guidance 导诊记录对象
     * @return 是否创建成功
     */
    @Override
    public boolean createGuidance(MedicalGuidance guidance) {
        logger.info("开始创建导诊记录");
        try {
            // 参数验证
            if (guidance.getPatientId() == null) {
                logger.error("创建导诊记录失败：患者ID不能为空");
                throw new IllegalArgumentException("患者ID不能为空");
            }
            
            if (guidance.getSymptomsDescription() == null || guidance.getSymptomsDescription().trim().isEmpty()) {
                logger.error("创建导诊记录失败：症状描述不能为空");
                throw new IllegalArgumentException("症状描述不能为空");
            }
            
            // 设置默认值和时间
            Date now = new Date();
            guidance.setGuidanceTime(now);
            guidance.setUpdateTime(now);
            guidance.setStatus(1); // 默认状态：未就诊
            
            // 保存到数据库
            boolean result = save(guidance);
            logger.info("导诊记录创建{}", result ? "成功" : "失败");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("参数错误: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("创建导诊记录异常: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 基于症状推荐科室
     * @param symptoms 症状描述
     * @param description 补充说明
     * @return 推荐结果，包含科室列表、症状信息等
     */
    @Override
    public Map<String, Object> recommendDepartment(String symptoms, String description) {
        logger.info("开始推荐科室，症状：{}, 描述：{}", symptoms, description);
        Map<String, Object> result = new HashMap<>();
        
        // 简化的AI推荐逻辑（实际项目中应接入AI模型）
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // 根据症状关键词进行匹配，使用更精确的关键词匹配
        String text = (symptoms + " " + description).toLowerCase();
        
        // 神经内科关键词匹配
        if (containsKeywords(text, "头痛", "头晕", "偏头痛", "癫痫", "中风", "脑出血", "脑梗塞", 
                           "帕金森", "痴呆", "记忆力减退", "失眠", "焦虑", "抑郁", "神经痛")) {
            addRecommendation(recommendations, 1L, "神经内科", "您的症状可能与神经系统相关，建议就诊神经内科进行详细检查");
        }
        
        // 消化内科关键词匹配
        if (containsKeywords(text, "腹痛", "胃疼", "恶心", "呕吐", "腹泻", "便秘", "消化不良", 
                           "胃酸", "胀气", "胃溃疡", "胃炎", "肝炎", "胆囊", "胰腺")) {
            addRecommendation(recommendations, 2L, "消化内科", "您的症状可能与消化系统相关，建议就诊消化内科进行诊断");
        }
        
        // 呼吸内科关键词匹配
        if (containsKeywords(text, "发热", "咳嗽", "喉咙痛", "感冒", "肺炎", "哮喘", "支气管炎", 
                           "呼吸困难", "胸闷", "气短", "结核", "肺癌", "气胸")) {
            addRecommendation(recommendations, 3L, "呼吸内科", "您的症状可能与呼吸系统相关，建议就诊呼吸内科");
        }
        
        // 心血管内科关键词匹配
        if (containsKeywords(text, "心脏", "胸闷", "胸痛", "心悸", "心律失常", "高血压", "低血压", 
                           "冠心病", "心肌梗塞", "心力衰竭", "心肌病")) {
            addRecommendation(recommendations, 4L, "心血管内科", "您的症状可能与心血管系统相关，建议就诊心血管内科");
        }
        
        // 骨科关键词匹配
        if (containsKeywords(text, "关节", "骨头", "骨折", "疼痛", "关节炎", "骨质疏松", "腰椎间盘", 
                           "颈椎", "扭伤", "拉伤", "骨肿瘤", "骨髓炎")) {
            addRecommendation(recommendations, 5L, "骨科", "您的症状可能与骨骼系统相关，建议就诊骨科");
        }
        
        // 眼科关键词匹配
        if (containsKeywords(text, "眼睛", "视力", "近视", "远视", "白内障", "青光眼", "结膜炎", 
                           "角膜炎", "眼干", "眼涩", "流泪", "视网膜", "弱视")) {
            addRecommendation(recommendations, 6L, "眼科", "您的症状可能与眼睛相关，建议就诊眼科");
        }
        
        // 耳鼻喉科关键词匹配
        if (containsKeywords(text, "耳朵", "听力", "耳鸣", "耳聋", "中耳炎", "鼻炎", "鼻窦炎", 
                           "咽喉炎", "扁桃体", "声音嘶哑", "打鼾", "眩晕")) {
            addRecommendation(recommendations, 7L, "耳鼻喉科", "您的症状可能与耳鼻喉相关，建议就诊耳鼻喉科");
        }
        
        // 皮肤科关键词匹配
        if (containsKeywords(text, "皮肤", "过敏", "疹子", "痘痘", "湿疹", "皮炎", "荨麻疹", 
                           "癣", "痤疮", "色素沉着", "白斑", "红斑", "脱发")) {
            addRecommendation(recommendations, 8L, "皮肤科", "您的症状可能与皮肤相关，建议就诊皮肤科");
        }
        
        // 全科医学科关键词匹配
        if (containsKeywords(text, "体检", "常规检查", "健康咨询", "慢性病管理", "综合治疗", 
                           "亚健康", "调理", "保健")) {
            addRecommendation(recommendations, 9L, "全科医学科", "您的情况建议就诊全科医学科进行全面检查");
        }
        
        // 如果没有匹配到特定科室，推荐全科
        if (recommendations.isEmpty()) {
            addRecommendation(recommendations, 9L, "全科医学科", "根据您的描述，建议先到全科医学科进行初步诊断");
        }
        
        // 补充获取科室详细信息
        for (Map<String, Object> rec : recommendations) {
            try {
                Long deptId = (Long) rec.get("deptId");
                Department dept = departmentMapper.selectById(deptId);
                if (dept != null) {
                    rec.put("deptDescription", dept.getDescription());
                    rec.put("deptName", dept.getDeptName());
                    rec.put("status", dept.getStatus());
                }
            } catch (Exception e) {
                // 忽略异常，确保程序继续执行
            }
        }
        
        result.put("recommendations", recommendations);
        result.put("symptoms", symptoms);
        result.put("description", description);
        result.put("timestamp", new Date());
        
        return result;
    }
    
    /**
     * 检查文本是否包含任意一个关键词
     * @param text 待检查的文本
     * @param keywords 关键词数组
     * @return 是否包含任意关键词
     */
    private boolean containsKeywords(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取导诊记录详情
     * @param guidanceId 导诊记录ID
     * @return 导诊记录对象
     */
    @Override
    public MedicalGuidance getGuidanceDetail(Long guidanceId) {
        logger.info("查询导诊记录详情，ID: {}", guidanceId);
        return getById(guidanceId);
    }

    /**
     * 更新导诊记录状态
     * @param guidanceId 导诊记录ID
     * @param status 状态码（1:未就诊, 2:已就诊, 3:已取消）
     * @return 是否更新成功
     */
    @Override
    public boolean updateGuidanceStatus(Long guidanceId, Integer status) {
        logger.info("更新导诊记录状态，ID: {}, 新状态: {}", guidanceId, status);
        MedicalGuidance guidance = getById(guidanceId);
        if (guidance == null) {
            logger.error("导诊记录不存在，ID: {}", guidanceId);
            throw new RuntimeException("导诊记录不存在");
        }
        
        // 验证状态值的有效性
        if (status != 1 && status != 2 && status != 3) {
            logger.error("无效的状态值: {}", status);
            throw new RuntimeException("无效的状态值");
        }
        
        // 更新状态和时间
        guidance.setStatus(status);
        guidance.setUpdateTime(new Date());
        
        boolean result = updateById(guidance);
        logger.info("导诊记录状态更新{}", result ? "成功" : "失败");
        return result;
    }

    /**
     * 添加推荐科室到结果列表
     * @param recommendations 推荐列表
     * @param deptId 科室ID
     * @param deptName 科室名称
     * @param reason 推荐原因
     */
    private void addRecommendation(List<Map<String, Object>> recommendations, Long deptId, String deptName, String reason) {
        Map<String, Object> item = new HashMap<>();
        item.put("deptId", deptId);
        item.put("deptName", deptName);
        item.put("reason", reason);
        item.put("confidence", 0.8); // 模拟置信度
        recommendations.add(item);
    }
}
