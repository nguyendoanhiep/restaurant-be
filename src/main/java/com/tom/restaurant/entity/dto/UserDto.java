package com.tom.restaurant.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String numberPhone;
    private String email;
    private Integer age;
    private String gender;
    private String city;
    private Integer status;
    private Date createDate;
    private Date modifiedDate;
    private Long imageId;

    public UserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}