package com.example.demo.unit.repository;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// 단위 테스트 (Repository) - DB 관련된 Bean이 IoC에 등록되면 됨
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 내장 DB (가짜 DB)로 테스트를 수행 - 단위 테스트
// replace = Replace.NONE : 실제 DB로 테스트 - 통합 테스트
@DataJpaTest // JPA 관련 얘들만 메모리에 띄움 - repository 들을 IoC에 등록해둠. - 특정 repository 를 메모리에 띄우는 속성이 있음
public class PostRepositoryUnitTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void init() {
        // 테이블 autoincrement 초기화
        // H2
        entityManager.createNativeQuery("ALTER TABLE post ALTER COLUMN id RESTART WITH 1").executeUpdate();
        // MySQL - entityManager.createNativeQuery("ALTER TABLE post AUTO_INCREMENT =1").executeUpdate();
    }

    @Test
    public void save_테스트() {
        // given
        Post post = new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용");

        // when
        Post postEntity = postRepository.save(post);

        // then
        assertEquals("스프링부트 따라하기", postEntity.getTitle());
    }

    @Test
    public void saveAll_테스트() {
        // given
        List<Post> postList = Arrays.asList(
                new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"),
                new Post(2L, "리엑트 따라하기", "리엑트 따라하기 내용")
        );
        // & when
        List<Post> postEntityList = postRepository.saveAll(postList);

        // then
        assertArrayEquals(postEntityList.toArray(), postList.toArray());

    }

    @Test
    public void findById_테스트() {
        // given
        postRepository.saveAll(
                Arrays.asList(
                        new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"),
                        new Post(2L, "리엑트 따라하기", "리엑트 따라하기 내용")
                )
        );
        Long id = 1L;

        // when
        Optional<Post> postEntity = postRepository.findById(id);

        // then
        assertTrue(postEntity.isPresent());
        assertEquals(1L, postEntity.get().getId());
        assertEquals("스프링부트 따라하기", postEntity.get().getTitle());

    }

    @Test
    public void findById_empty_테스트() {
        // given
        Long id = 1L;

        // when
        Optional<Post> postEntity = postRepository.findById(id);

        // then
        assertTrue(postEntity.isEmpty());
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
        assertNotEquals(0, postEntityList.size());
        assertEquals(2, postEntityList.size());
    }

    @Test
    public void deleteById_테스트() {
        // given
        postRepository.saveAll(
                Arrays.asList(
                        new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"),
                        new Post(2L, "리엑트 따라하기", "리엑트 따라하기 내용")
                )
        );
        Long id = 1L;


        // when
        postRepository.deleteById(id);
        Optional<Post> postEntity = postRepository.findById(id);

        // then
        assertTrue(postEntity.isEmpty());
    }
}
