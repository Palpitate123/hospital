package com.example.entity;

import lombok.Data;
import java.util.List;

/**
 * 首页统计数据实体类
 */
@Data
public class Statistics {
    private Integer todayRegister;    // 今日挂号总量（reserve表）
    private Integer todayCalled;      // 今日已叫号人数（reserve表）
    private Integer todayUnCalled;    // 今日未叫号人数（reserve表）
    private Integer doctorCount;      // 医生总数（doctor表，共6条）
    private Integer patientCount;     // 患者总数（user表，共4条）
    private Integer adminCount;       // 管理员总数（admin表，共1条）
    private List<Integer> weekList;   // 近7天挂号量（图表用）
}
