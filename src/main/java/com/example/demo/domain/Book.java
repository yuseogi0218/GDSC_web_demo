package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data // Getter, Setter 생성
@NoArgsConstructor
@AllArgsConstructor
@Entity // 서버 실행 시, Object Relation Mapping(ORM) 이 됨. (즉, 테이블이 DB에 생성 됨)
public class Book {

    @Id // PK를 해당 변수로 하겠다는 뜻.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 데이터베이스 번호 증가 전략을 따라가겠다 (H2 DB 전략을 따라감)
    private Long id;

    private String title;

    private String author;
}
