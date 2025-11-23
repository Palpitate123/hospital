package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Department;
import com.hospital.mapper.DepartmentMapper;
import com.hospital.mapper.DoctorMapper;
import com.hospital.service.DepartmentService;
import com.hospital.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科室服务实现类
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<Department> getAllDepartments() {
        return departmentMapper.selectList(null);
    }

    @Override
    public Department getDepartmentById(Long deptId) {
        return departmentMapper.selectById(deptId);
    }

    @Transactional
    @Override
    public boolean addDepartment(Department department) {
        // 设置默认状态
        department.setStatus(1);
        return save(department);
    }

    @Transactional
    @Override
    public boolean updateDepartment(Department department) {
        return updateById(department);
    }

    @Transactional
    @Override
    public boolean deleteDepartment(Long deptId) {
        // 检查是否有医生属于该科室
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.hospital.entity.Doctor> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("dept_id", deptId);
        Long doctorCount = doctorMapper.selectCount(queryWrapper);
        if (doctorCount > 0) {
            throw new RuntimeException("该科室下还有医生，无法删除");
        }
        return removeById(deptId);
    }

    @Override
    public Map<String, Object> getDepartmentDetail(Long deptId) {
        Map<String, Object> detail = new HashMap<>();
        
        // 获取科室基本信息
        Department department = getById(deptId);
        if (department == null) {
            return detail;
        }
        
        detail.put("department", department);
        
        // 获取医生数量
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.hospital.entity.Doctor> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("dept_id", deptId);
        Long doctorCount = doctorMapper.selectCount(queryWrapper);
        detail.put("doctorCount", doctorCount);
        
        // 获取出诊医生数量
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.hospital.entity.Doctor> availableWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        availableWrapper.eq("dept_id", deptId).eq("status", 1);
        Long availableDoctorCount = doctorMapper.selectCount(availableWrapper);
        detail.put("availableDoctorCount", availableDoctorCount);
        
        return detail;
    }

    @Override
    public List<Department> searchDepartments(String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Department> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.like("dept_name", keyword).or().like("description", keyword);
        return departmentMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<DoctorVO> getDepartmentDoctors(Long deptId) {
        return doctorMapper.selectDoctorVOsByDeptId(deptId);
    }
}
