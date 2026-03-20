package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.DoctorDTO;
import com.hospital.entity.HosDepartment;
import com.hospital.entity.HosDoctor;
import com.hospital.entity.SysUser;
import com.hospital.entity.SysUserRole;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.HosDepartmentMapper;
import com.hospital.mapper.HosDoctorMapper;
import com.hospital.mapper.SysUserMapper;
import com.hospital.mapper.SysUserRoleMapper;
import com.hospital.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 * 实现医生管理相关业务逻辑
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class DoctorServiceImpl extends ServiceImpl<HosDoctorMapper, HosDoctor> implements DoctorService {

    @Autowired
    private HosDoctorMapper hosDoctorMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private HosDepartmentMapper hosDepartmentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询医生列表
     */
    @Override
    public Page<Map<String, Object>> getDoctorPage(Page<HosDoctor> page, Long deptId, String doctorName, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("doctorName", doctorName);
        params.put("status", status);

        List<Map<String, Object>> records = hosDoctorMapper.selectDoctorWithDept(params);

        // 手动分页
        int total = records.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        List<Map<String, Object>> pageRecords = records.subList(start, end);

        Page<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), total);
        resultPage.setRecords(pageRecords);
        return resultPage;
    }

    /**
     * 查询科室下的医生列表
     */
    @Override
    public List<Map<String, Object>> getDoctorsByDeptId(Long deptId) {
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("status", 1);
        return hosDoctorMapper.selectDoctorWithDept(params);
    }

    /**
     * 获取医生详情
     */
    @Override
    public Map<String, Object> getDoctorDetail(Long id) {
        Map<String, Object> detail = hosDoctorMapper.selectDoctorDetailById(id);
        if (detail == null) {
            throw new BusinessException("医生不存在");
        }
        return detail;
    }

    /**
     * 新增医生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addDoctor(DoctorDTO doctorDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUsername, doctorDTO.getUsername());
        if (sysUserMapper.selectCount(userWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查科室是否存在
        HosDepartment dept = hosDepartmentMapper.selectById(doctorDTO.getDeptId());
        if (dept == null) {
            throw new BusinessException("科室不存在");
        }

        // 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(doctorDTO.getUsername());
        user.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        user.setNickName(doctorDTO.getDoctorName());
        user.setStatus(doctorDTO.getStatus() != null ? doctorDTO.getStatus() : 1);
        user.setRemark("管理员创建的医生账号");
        sysUserMapper.insert(user);

        // 分配医生角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(2L); // 医生角色ID
        sysUserRoleMapper.insert(userRole);

        // 创建医生信息
        HosDoctor doctor = new HosDoctor();
        BeanUtils.copyProperties(doctorDTO, doctor);
        doctor.setUserId(user.getId());
        doctor.setStatus(doctorDTO.getStatus() != null ? doctorDTO.getStatus() : 1);
        this.save(doctor);

        log.info("新增医生成功: {}", doctor.getDoctorName());
        return true;
    }

    /**
     * 编辑医生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDoctor(DoctorDTO doctorDTO) {
        HosDoctor doctor = this.getById(doctorDTO.getId());
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 更新医生信息
        doctor.setDeptId(doctorDTO.getDeptId());
        doctor.setDoctorName(doctorDTO.getDoctorName());
        doctor.setTitle(doctorDTO.getTitle());
        doctor.setSpecialty(doctorDTO.getSpecialty());
        doctor.setDoctorDesc(doctorDTO.getDoctorDesc());
        doctor.setAvatar(doctorDTO.getAvatar());
        if (doctorDTO.getStatus() != null) {
            doctor.setStatus(doctorDTO.getStatus());
        }
        this.updateById(doctor);

        // 同步更新用户昵称
        SysUser user = sysUserMapper.selectById(doctor.getUserId());
        if (user != null) {
            user.setNickName(doctorDTO.getDoctorName());
            if (doctorDTO.getStatus() != null) {
                user.setStatus(doctorDTO.getStatus());
            }
            sysUserMapper.updateById(user);
        }

        log.info("编辑医生成功: {}", doctor.getDoctorName());
        return true;
    }

    /**
     * 删除医生
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDoctor(Long id) {
        HosDoctor doctor = this.getById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 删除医生信息
        this.removeById(id);

        // 删除用户账号
        sysUserMapper.deleteById(doctor.getUserId());

        log.info("删除医生成功: {}", doctor.getDoctorName());
        return true;
    }

    /**
     * 启用/停用医生账号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDoctorStatus(Long id, Integer status) {
        HosDoctor doctor = this.getById(id);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        doctor.setStatus(status);
        this.updateById(doctor);

        // 同步更新用户状态
        SysUser user = sysUserMapper.selectById(doctor.getUserId());
        if (user != null) {
            user.setStatus(status);
            sysUserMapper.updateById(user);
        }

        log.info("医生账号状态更新: doctorId={}, status={}", id, status);
        return true;
    }
}
