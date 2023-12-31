package com.tom.restaurant.service.impl;

import com.tom.restaurant.entity.*;
import com.tom.restaurant.entity.dto.FormChangePassword;
import com.tom.restaurant.entity.dto.FormRegister;
import com.tom.restaurant.entity.dto.UserRequest;
import com.tom.restaurant.entity.dto.FormLogin;
import com.tom.restaurant.jwt.JwtTokenProvider;
import com.tom.restaurant.repository.*;
import com.tom.restaurant.response.Details;
import com.tom.restaurant.response.Response;
import com.tom.restaurant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    VoucherCustomerRepository voucherCustomerRepository;

    @Override
    public Response<?> getAll(Pageable pageable, String search, Integer status) {
        Page<User> listUser = userRepository.getAll(pageable, search, status);
        return Response.SUCCESS(listUser);
    }

    @Override
    @Transactional
    public Response<?> register(FormRegister formRegister) {
        try {
            Optional<User> checkUserExists = userRepository.findByUsername(formRegister.getUsername());
            if (checkUserExists.isPresent()) {
                return Response.FAIL(Details.DATA_ALREADY_EXISTS);
            }
            Set<Role> roles = new HashSet<>();
            Optional<Role> role = roleRepository.findByName("ROLE_USER");
            role.ifPresent(roles::add);
            userRepository.save(User.builder()
                    .id(null)
                    .username(formRegister.getUsername())
                    .password(passwordEncoder.encode(formRegister.getPassword()))
                    .numberPhone(formRegister.getNumberPhone())
                    .roles(roles)
                    .status(1)
                    .createDate(new Date())
                    .modifiedDate(new Date())
                    .build());
            Customer customerIsExits = customerRepository.findByNumberPhone(formRegister.getNumberPhone());
            if (customerIsExits == null) {
                customerRepository.save(Customer
                        .builder()
                        .id(null)
                        .numberPhone(formRegister.getNumberPhone())
                        .status(1)
                        .loyaltyPoints(0L)
                        .createDate(new Date())
                        .modifiedDate(new Date())
                        .build());
            }
            return Response.SUCCESS(true);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.FAIL(false);
        }
    }

    @Override
    public Response<?> login(FormLogin formLogin) {
        try {
            Optional<User> user = userRepository.findByUsername(formLogin.getUsername());
            if (!user.isPresent()) {
                return Response.FAIL(Details.DATA_NOT_FOUND);
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            formLogin.getUsername(),
                            formLogin.getPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
            return Response.SUCCESS(jwt);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.FAIL(false);
        }
    }

    @Override
    @Transactional
    public Response<?> editUser(UserRequest request) {
        try {
            Set<Role> roles = new HashSet<>();
            request.getRoles().forEach(data -> {
                Role role = roleRepository.findByName(data).orElseThrow(() -> new RuntimeException("Role not found"));
                roles.add(role);
            });
            User user = userRepository.findById(request.getId()).get();
            if (!Objects.equals(user.getNumberPhone(), request.getNumberPhone())) {
                customerRepository.updateNumberPhone(user.getNumberPhone(), request.getNumberPhone());
                orderRepository.updateNumberPhone(user.getNumberPhone(), request.getNumberPhone());
                voucherCustomerRepository.updateNumberPhone(user.getNumberPhone(), request.getNumberPhone());
            }
            user.setNumberPhone(request.getNumberPhone());
            user.setStatus(request.getStatus());
            user.setRoles(roles);
            user.setModifiedDate(new Date());
            userRepository.save(user);
            return Response.SUCCESS(true);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.FAIL(false);
        }

    }

    @Override
    public Response<?> changePassword(FormChangePassword formChangePassword) {
        try {
            User user = userRepository.findById(formChangePassword.getId()).get();
            boolean isPasswordMatch = passwordEncoder.matches(formChangePassword.getCurrentPassword(), user.getPassword());
            if (!isPasswordMatch) {
                return Response.FAIL(Details.PASSWORD_NO_MATCH);
            }
            user.setPassword(passwordEncoder.encode(formChangePassword.getNewPassword()));
            userRepository.save(user);
            return Response.SUCCESS(true);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.FAIL(false);
        }
    }
}
