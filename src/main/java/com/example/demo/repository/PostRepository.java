package com.example.demo.repository;

import com.example.demo.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 적어야 스프링 IOC에 빈으로 등록이 되는데,
// JpaRepository를 extends 하면 생랼 가능 (자동으로 빈에 등록 됨)
// JpaRepository는 CRUD 함수를 들고 있음
public interface PostRepository extends JpaRepository<Post, Long> {
}