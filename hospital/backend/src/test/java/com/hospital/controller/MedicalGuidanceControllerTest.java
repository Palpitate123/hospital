package com.hospital.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalGuidanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRecommendDepartment() throws Exception {
        // 测试科室推荐功能
        mockMvc.perform(MockMvcRequestBuilders.get("/medical-guidance/recommend")
                .param("symptoms", "头痛 发热")
                .param("description", "持续头痛两天，伴有轻微发热"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.recommendations").isArray());
    }

    @Test
    public void testGetGuidanceHistory() throws Exception {
        // 测试获取导诊历史
        mockMvc.perform(MockMvcRequestBuilders.get("/medical-guidance/history")
                .sessionAttr("userId", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0));
    }
}