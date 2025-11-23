package com.hospital.vo;

import lombok.Data;
import java.util.Date;

/**
 * 患者信息视图对象
 */
@Data
public class PatientVO {
    private Long id;
    private String realName;
    private String gender;
    private String phone;
    private String email;
    private Date birthday;
    private String address;
    private Date createTime;
    
    // 隐私信息脱敏处理方法
    public String getMaskedPhone() {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 3) {
            return parts[0].charAt(0) + "****@" + parts[1];
        }
        return parts[0].substring(0, 3) + "****@" + parts[1];
    }
}
