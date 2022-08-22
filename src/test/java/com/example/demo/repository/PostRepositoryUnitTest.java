package com.example.demo.repository;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// 단위 테스트 (Repository) - DB 관련된 Bean이 IoC에 등록되면 됨
@Slf4j
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 내장 DB (가짜 DB)로 테스트를 수행 - 단위 테스트
// replace = Replace.NONE : 실제 DB로 테스트 - 통합 테스트
@DataJpaTest // JPA 관련 얘들만 메모리에 띄움 - repository 들을 IoC에 등록해둠. - 특정 repository 를 메모리에 띄우는 속성이 있음
public class PostRepositoryUnitTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void save_테스트() {
        // given
        Post post = new Post(null, "게시글 제목", "게시글 내용");

        // when
        Post postEntity = postRepository.save(post);

        // then
        assertEquals("게시글 제목", postEntity.getTitle());
    }

    @Test
    public void findAll_테스트() {
        // given
        postRepository.saveAll(
                Arrays.asList(
                        new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용"),
                        new Post(null, "리엑트 따라하기", "리엑트 따라하기 내용")
                )
        );

        // when
        List<Post> postEntityList = postRepository.findAll();

        // then
        log.info("postEntityList : "+postEntityList.size() );
        assertNotEquals(0, postEntityList.size());
        assertEquals(2, postEntityList.size());
    }
}
