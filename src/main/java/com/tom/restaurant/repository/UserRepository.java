package com.tom.restaurant.repository;

import com.tom.restaurant.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("select u from User u where (:search is null or u.username like :search% or u.numberPhone like :search%) and (:status = 0 or u.status = :status)")

    Page<User> getAll(Pageable pageable, String search, Integer status);

    @Modifying
    @Query("UPDATE User u SET u.numberPhone = :newNumberPhone WHERE u.numberPhone = :oldNumberPhone")
    void updateNumberPhone(String oldNumberPhone, String newNumberPhone);
}
