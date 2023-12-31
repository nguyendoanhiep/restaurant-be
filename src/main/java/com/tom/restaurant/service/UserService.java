package com.tom.restaurant.service;

import com.tom.restaurant.entity.dto.FormChangePassword;
import com.tom.restaurant.entity.dto.FormRegister;
import com.tom.restaurant.entity.dto.UserRequest;
import com.tom.restaurant.entity.dto.FormLogin;
import com.tom.restaurant.response.Response;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Response<?> getAll(Pageable pageable,String search , Integer status);

    Response<?> register(FormRegister formRegister);
    Response<?> login(FormLogin formLogin);
    Response<?> editUser(UserRequest request);
    Response<?> changePassword(FormChangePassword formChangePassword);
}
