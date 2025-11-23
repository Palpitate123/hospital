package com.hospital.controller;

import com.hospital.entity.Appointment;
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
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPatientAppointments() throws Exception {
        // 测试获取患者预约列表
        mockMvc.perform(MockMvcRequestBuilders.get("/appointment/patient")
                .sessionAttr("userId", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testGetDoctorAppointments() throws Exception {
        // 测试获取医生预约列表
        mockMvc.perform(MockMvcRequestBuilders.get("/appointment/doctor")
                .sessionAttr("userId", 2L)) // 假设用户ID为2的是医生
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0));
    }
}