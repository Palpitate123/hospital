package com.example.service;

import com.example.entity.Statistics;
import com.example.mapper.HomeMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 首页统计服务层
 */
@Service
public class HomeService {

    @Resource
    private HomeMapper homeMapper;

    /**
     * 获取所有统计数据
     * @return 统计结果
     */
    public Statistics getStatistics() {
        Statistics s = new Statistics();
        // 今日挂号总量
        s.setTodayRegister(homeMapper.getTodayRegister());
        // 今日已叫号人数
        s.setTodayCalled(homeMapper.getTodayCalled());
        // 今日未叫号人数
        s.setTodayUnCalled(homeMapper.getTodayUnCalled());
        // 医生总数
        s.setDoctorCount(homeMapper.getDoctorCount());
        // 患者总数
        s.setPatientCount(homeMapper.getPatientCount());
        // 管理员总数
        s.setAdminCount(homeMapper.getAdminCount());
        // 近7天挂号量
        s.setWeekList(homeMapper.getWeekRegister());
        return s;
    }
}
