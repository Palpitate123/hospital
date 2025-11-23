package com.hospital.controller;

import com.hospital.entity.MedicalGuidance;
import com.hospital.service.MedicalGuidanceService;
import com.hospital.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 医疗导诊控制器
 */
@RestController
@RequestMapping("/api/guidance")
public class MedicalGuidanceController {

    @Autowired
    private MedicalGuidanceService medicalGuidanceService;

    /**
     * AI推荐科室
     */
    @PostMapping("/recommend")
    public Result recommendDepartment(@RequestBody Map<String, String> request) {
        String symptoms = request.get("symptoms");
        String description = request.get("description");
        
        if (symptoms == null || symptoms.isEmpty()) {
            return Result.fail("请描述您的症状");
        }
        
        Map<String, Object> recommendation = medicalGuidanceService.recommendDepartment(symptoms, description);
        return Result.success(recommendation);
    }

    /**
     * 保存导诊记录
     */
    @PostMapping("/save")
    public Result saveGuidance(@RequestBody MedicalGuidance guidance, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            guidance.setPatientId(userId);
            
            // 参数验证
            if (guidance.getPatientId() == null) {
                return Result.fail("患者ID不能为空");
            }
            
            if (guidance.getSymptomsDescription() == null || guidance.getSymptomsDescription().isEmpty()) {
                return Result.fail("症状描述不能为空");
            }
            
            // 设置创建时间和默认状态
            guidance.setGuidanceTime(new java.util.Date());
            guidance.setUpdateTime(new java.util.Date());
            guidance.setStatus(1); // 默认状态：未就诊
            
            boolean result = medicalGuidanceService.createGuidance(guidance);
            if (result) {
                Map<String, Object> data = new HashMap<>();
                data.put("guidanceId", guidance.getId());
                return Result.success(data);
            } else {
                return Result.fail("保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户的导诊记录
     */
    @GetMapping("/history")
    public Result getGuidanceHistory(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<MedicalGuidance> history = medicalGuidanceService.getGuidanceByPatientId(userId);
            
            // 转换为包含状态文本的响应数据
            List<Map<String, Object>> formattedHistory = new ArrayList<>();
            for (MedicalGuidance guidance : history) {
                Map<String, Object> item = new HashMap<>();
                BeanUtils.copyProperties(guidance, item);
                item.put("statusText", getStatusText(guidance.getStatus()));
                formattedHistory.add(item);
            }
            
            return Result.success(formattedHistory);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("获取导诊历史失败：" + e.getMessage());
        }
    }

    /**
     * 获取导诊详情
     */
    @GetMapping("/detail/{guidanceId}")
    public Result getGuidanceDetail(@PathVariable Long guidanceId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MedicalGuidance guidance = medicalGuidanceService.getGuidanceDetail(guidanceId);
        
        // 验证权限：只能查看自己的导诊记录或管理员可以查看所有
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode) && !guidance.getPatientId().equals(userId)) {
            return Result.fail("无权查看该导诊记录");
        }
        
        return Result.success(guidance);
    }

    /**
     * 更新导诊状态
     */
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> request, HttpServletRequest req) {
        try {
            Long guidanceId = Long.valueOf(request.get("guidanceId").toString());
            Integer status = (Integer) request.get("status");
            
            // 验证状态值
            if (status == null || (status != 1 && status != 2 && status != 3)) {
                return Result.fail("无效的状态值，状态必须为1(未就诊)、2(已就诊)或3(已忽略)");
            }
            
            boolean result = medicalGuidanceService.updateGuidanceStatus(guidanceId, status);
            return result ? Result.success() : Result.fail("更新失败，请检查导诊记录是否存在");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取导诊首页推荐
     */
    @GetMapping("/home-recommendations")
    public Result getHomeRecommendations() {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("commonDepartments", getCommonDepartments());
        data.put("healthTips", getHealthTips());
        return Result.success(data);
    }

    /**
     * 获取常见科室
     */
    private List<Map<String, Object>> getCommonDepartments() {
        List<Map<String, Object>> departments = new java.util.ArrayList<>();
        addDepartment(departments, 1L, "神经内科", "头痛、头晕、记忆力减退等");
        addDepartment(departments, 2L, "消化内科", "胃痛、腹泻、消化不良等");
        addDepartment(departments, 3L, "呼吸内科", "咳嗽、发热、胸闷等");
        addDepartment(departments, 4L, "心血管内科", "心悸、胸痛、高血压等");
        addDepartment(departments, 9L, "全科医学科", "常规体检、慢病管理等");
        return departments;
    }

    /**
     * 添加科室到列表
     */
    private void addDepartment(List<Map<String, Object>> departments, Long id, String name, String description) {
        Map<String, Object> dept = new java.util.HashMap<>();
        dept.put("id", id);
        dept.put("name", name);
        dept.put("description", description);
        departments.add(dept);
    }

    /**
     * 获取健康小贴士
     */
    private List<String> getHealthTips() {
        List<String> tips = new java.util.ArrayList<>();
        tips.add("保持充足的睡眠，每天7-8小时为宜");
        tips.add("均衡饮食，多吃蔬菜水果，少吃油腻食物");
        tips.add("适量运动，每周至少150分钟中等强度有氧运动");
        tips.add("定期体检，早发现早治疗");
        tips.add("保持心情愉悦，减轻压力");
        return tips;
    }
    
    // 获取状态对应的文本描述
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "未就诊";
            case 2:
                return "已就诊";
            case 3:
                return "已忽略";
            default:
                return "未知";
        }
    }
}
