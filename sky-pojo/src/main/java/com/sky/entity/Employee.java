package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;                // 员工ID
    private String username;        // 用户名
    private String name;            // 姓名
    private String password;        // 密码
    private String phone;           // 手机号
    private String sex;             // 性别
    private String idNumber;        // 身份证号
    private Integer status;         // 状态（1正常 0锁定）
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    private Long createUser;        // 创建人ID
    private Long updateUser;        // 更新人ID


}
