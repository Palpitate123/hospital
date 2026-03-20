package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.DepartmentDTO;
import com.hospital.entity.HosDepartment;
import com.hospital.entity.HosDoctor;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.HosDepartmentMapper;
import com.hospital.mapper.HosDoctorMapper;
import com.hospital.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 科室服务实现类
 * 实现科室管理相关业务逻辑
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<HosDepartmentMapper, HosDepartment> implements DepartmentService {

    @Autowired
    private HosDoctorMapper hosDoctorMapper;

    /**
     * 分页查询科室列表
     */
    @Override
    public Page<HosDepartment> getDepartmentPage(Page<HosDepartment> page, String deptName, Integer status) {
        LambdaQueryWrapper<HosDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(deptName), HosDepartment::getDeptName, deptName);
        wrapper.eq(status != null, HosDepartment::getStatus, status);
        wrapper.orderByDesc(HosDepartment::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 查询所有启用的科室列表
     */
    @Override
    public List<HosDepartment> getEnabledDepartments() {
        LambdaQueryWrapper<HosDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HosDepartment::getStatus, 1);
        wrapper.orderByAsc(HosDepartment::getDeptName);
        return this.list(wrapper);
    }

    /**
     * 新增科室
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDepartment(DepartmentDTO departmentDTO) {
        // 检查科室名称是否已存在
        LambdaQueryWrapper<HosDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HosDepartment::getDeptName, departmentDTO.getDeptName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("科室名称已存在");
        }

        HosDepartment department = new HosDepartment();
        BeanUtils.copyProperties(departmentDTO, department);
        department.setStatus(1);

        this.save(department);
        log.info("新增科室成功: {}", department.getDeptName());
        return true;
    }

    /**
     * 编辑科室
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartment(DepartmentDTO departmentDTO) {
        HosDepartment department = this.getById(departmentDTO.getId());
        if (department == null) {
            throw new BusinessException("科室不存在");
        }

        // 检查科室名称是否重复
        if (!department.getDeptName().equals(departmentDTO.getDeptName())) {
            LambdaQueryWrapper<HosDepartment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HosDepartment::getDeptName, departmentDTO.getDeptName());
            wrapper.ne(HosDepartment::getId, departmentDTO.getId());
            if (this.count(wrapper) > 0) {
                throw new BusinessException("科室名称已存在");
            }
        }

        department.setDeptName(departmentDTO.getDeptName());
        department.setDeptDesc(departmentDTO.getDeptDesc());

        this.updateById(department);
        log.info("编辑科室成功: {}", department.getDeptName());
        return true;
    }

    /**
     * 删除科室
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDepartment(Long id) {
        HosDepartment department = this.getById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }

        // 检查是否有关联医生
        int doctorCount = hosDoctorMapper.countByDeptId(id);
        if (doctorCount > 0) {
            throw new BusinessException("科室已关联医生，无法删除");
        }

        this.removeById(id);
        log.info("删除科室成功: {}", department.getDeptName());
        return true;
    }

    /**
     * 启用/停用科室
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDepartmentStatus(Long id, Integer status) {
        HosDepartment department = this.getById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }

        department.setStatus(status);
        this.updateById(department);
        log.info("科室状态更新: deptId={}, status={}", id, status);
        return true;
    }

    /**
     * 获取科室详情
     */
    @Override
    public HosDepartment getDepartmentDetail(Long id) {
        HosDepartment department = this.getById(id);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }
        return department;
    }
}
