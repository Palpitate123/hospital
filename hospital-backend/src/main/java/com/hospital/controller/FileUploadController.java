package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 处理图片、文件上传请求
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "文件上传")
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 允许上传的图片类型
     */
    private static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传图片
     * 统一的图片上传接口，支持预览、格式验证
     * 
     * @param file 图片文件
     * @return 图片访问路径
     */
    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        // 验证文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        // 获取文件扩展名
        String fileExtension = getFileExtension(originalFilename);
        if (fileExtension == null) {
            throw new BusinessException("无法识别文件类型");
        }

        // 验证文件类型
        if (!isAllowedImageType(fileExtension)) {
            throw new BusinessException("只支持上传 jpg、jpeg、png、gif、bmp、webp 格式的图片");
        }

        // 生成新文件名（UUID + 扩展名）
        String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExtension;

        // 按日期创建子目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullPath = uploadPath + datePath + "/";

        // 创建目录
        File directory = new File(fullPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 保存文件
        File destFile = new File(fullPath + newFileName);
        try {
            file.transferTo(destFile);
            log.info("文件上传成功: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new BusinessException("文件上传失败，请重试");
        }

        // 返回文件访问路径
        Map<String, Object> result = new HashMap<>();
        result.put("fileName", newFileName);
        result.put("filePath", datePath + "/" + newFileName);
        result.put("fileUrl", "/api/file/preview/" + datePath + "/" + newFileName);
        result.put("fileSize", file.getSize());

        return Result.success("上传成功", result);
    }

    /**
     * 上传头像
     * 专门用于头像上传，增加裁剪建议
     * 
     * @param file 头像文件
     * @return 头像访问路径
     */
    @ApiOperation("上传头像")
    @PostMapping("/upload/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Result<Map<String, Object>> result = uploadImage(file);
        // 可以在这里添加头像裁剪逻辑
        return result;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 验证文件类型是否允许
     */
    private boolean isAllowedImageType(String fileExtension) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }
}
