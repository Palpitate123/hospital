package com.example.controller;

import com.example.entity.Statistics;
import com.example.service.HomeService;
import com.example.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页统计接口控制器
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeService homeService;

    /**
     * 获取首页统计数据
     * @return 统计结果
     */
    @GetMapping("/statistics")
    public Result getStatistics() {
        Statistics statistics = homeService.getStatistics();
        return Result.success(statistics);
    }
}
