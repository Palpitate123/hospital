package com.example.mapper;
import com.example.entity.Doctor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;
/**
 * 操作doctor相关数据接口
 */
public interface DoctorMapper {
    /**
     * 新增
     */
    int insert(Doctor doctor);
    /**
     * 删除
     */
    int deleteById(Integer id);
    /**
     * 修改
     */
    int updateById(Doctor doctor);
    /**
     * 根据ID查询
     */
    Doctor selectById(Integer id);
    /**
     * 查询所有
     */
    List<Doctor> selectAll(Doctor doctor);
    @Select("select * from doctor where username = #{username}")
    Doctor selectByUsername(String username);

    /**
     * 查询今日坐诊医生排班
     * @param week 今日星期几
     */
    List<Map<String, Object>> selectTodaySchedule(@Param("week") String week);
}
