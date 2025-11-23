package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hospital.entity.Doctor;
import com.hospital.entity.Department;
import com.hospital.entity.User;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.UserMapper;
import com.hospital.service.DoctorService;
import com.hospital.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 医生服务实现类
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
    
    // 职称映射表，定义为静态常量避免重复创建
    private static final Map<String, String> TITLE_MAP = new HashMap<String, String>() {{
        put("住院医师", "住院医师");
        put("主治医师", "主治医师");
        put("副主任医师", "副主任医师");
        put("主任医师", "主任医师");
    }};

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Doctor> getDoctorsByDeptId(Long deptId) {
        return doctorMapper.selectByDeptId(deptId);
    }
    
    @Override
    public List<DoctorVO> getDoctorVOsByDeptId(Long deptId) {
        List<Doctor> doctors = doctorMapper.selectByDeptId(deptId);
        return convertToVOList(doctors);
    }
    
    @Override
    public List<DoctorVO> getAllDoctorVOs() {
        List<Doctor> doctors = list();
        return convertToVOList(doctors);
    }
    
    @Override
    public List<DoctorVO> searchDoctors(String keyword) {
        // 使用数据库查询进行搜索，避免内存中过滤所有医生
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        QueryWrapper<Doctor> queryWrapper = new QueryWrapper<>();
        
        // 构建或条件查询
        queryWrapper.or(wrapper -> wrapper
                .inSql("user_id", "SELECT id FROM user WHERE real_name LIKE CONCAT('%', #{keyword}, '%')")
                .or()
                .like("specialty", keyword)
                .or()
                .like("title", keyword)
                .or()
                .inSql("dept_id", "SELECT id FROM department WHERE dept_name LIKE CONCAT('%', #{keyword}, '%')")
        );
        
        List<Doctor> doctors = list(queryWrapper);
        logger.info("搜索完成，共找到 {} 位医生", doctors.size());
        return convertToVOList(doctors);
    }

    @Override
    public Doctor getDoctorByUserId(Long userId) {
        return doctorMapper.selectByUserId(userId);
    }
    
    @Override
    public DoctorVO getDoctorVOByUserId(Long userId) {
        Doctor doctor = doctorMapper.selectByUserId(userId);
        return doctor != null ? convertToVO(doctor) : null;
    }

    @Override
    public int getTodayAppointmentCount(Long doctorId) {
        return getAppointmentCountByDate(doctorId, new Date());
    }
    
    @Override
    public int getAppointmentCountByDate(Long doctorId, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        return doctorMapper.countTodayAppointments(doctorId, dateStr);
    }

    @Override
    public Map<String, Object> getDoctorDetail(Long doctorId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取医生信息
        Doctor doctor = getById(doctorId);
        if (doctor == null) {
            return result;
        }
        
        // 获取用户信息
        User user = userMapper.selectById(doctor.getUserId());
        // 获取科室信息
        Department department = departmentMapper.selectById(doctor.getDeptId());
        
        // 组装数据
        result.put("doctor", doctor);
        result.put("user", user);
        result.put("department", department);
        // 获取今日预约数量
        result.put("todayAppointmentCount", getTodayAppointmentCount(doctorId));
        
        return result;
    }
    
    @Override
    public DoctorVO getDoctorVOById(Long doctorId) {
        Doctor doctor = getById(doctorId);
        return doctor != null ? convertToVO(doctor) : null;
    }

    @Override
    public boolean updateDoctor(Doctor doctor) {
        return updateById(doctor);
    }

    @Override
    public boolean updateDoctorStatus(Long doctorId, Integer status) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStatus(status);
        return updateById(doctor);
    }
    
    /**
     * 将Doctor对象转换为DoctorVO对象
     */
    private DoctorVO convertToVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        // 复制基本属性
        vo.setId(doctor.getId());
        vo.setUserId(doctor.getUserId());
        vo.setDeptId(doctor.getDeptId());
        vo.setTitle(doctor.getTitle());
        vo.setSpecialty(doctor.getSpecialty());
        vo.setIntroduction(doctor.getIntroduction());
        vo.setExperience(doctor.getExperience());
        vo.setConsultationFee(doctor.getConsultationFee());
        vo.setDailyQuota(doctor.getDailyQuota());
        vo.setStatus(doctor.getStatus());
        vo.setCreateTime(doctor.getCreateTime());
        vo.setUpdateTime(doctor.getUpdateTime());
        
        // 设置用户信息
        User user = userMapper.selectById(doctor.getUserId());
        if (user != null) {
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
        }
        
        // 设置科室信息
        Department dept = departmentMapper.selectById(doctor.getDeptId());
        if (dept != null) {
            vo.setDeptName(dept.getDeptName());
        }
        
        // 设置统计信息
        int todayCount = getTodayAppointmentCount(doctor.getId());
        vo.setTodayAppointmentCount(todayCount);
        vo.setAvailableQuota(doctor.getDailyQuota() - todayCount);
        
        // 设置状态文本
        vo.setStatusText(doctor.getStatus() == 1 ? "出诊中" : "已停诊");
        
        // 设置职称文本
        vo.setTitleText(getTitleText(doctor.getTitle()));
        
        return vo;
    }
    
    /**
     * 将Doctor列表转换为DoctorVO列表
     */
    private List<DoctorVO> convertToVOList(List<Doctor> doctors) {
        return doctors.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取职称文本
     */
    private String getTitleText(String title) {
        return TITLE_MAP.getOrDefault(title, title);
    }
}
