package com.tom.restaurant.repository;

import com.tom.restaurant.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query(value = "select p from Post p where :search is null or p.name like %:search% ")
    Page<Post>getAll(Pageable pageable , String search);
}
