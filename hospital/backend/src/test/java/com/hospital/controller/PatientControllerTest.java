package com.hospital.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPatientInfo() throws Exception {
        // 模拟用户ID为1的患者获取个人信息
        mockMvc.perform(MockMvcRequestBuilders.get("/patient/info")
                .sessionAttr("userId", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    public void testGetPatientInfoById() throws Exception {
        // 模拟管理员/医生获取患者ID为1的信息
        mockMvc.perform(MockMvcRequestBuilders.get("/patient/info/1")
                .sessionAttr("userRole", "admin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }
}