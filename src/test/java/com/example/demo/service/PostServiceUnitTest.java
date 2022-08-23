package com.example.demo.service;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.dto.post.WritePostReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// 단위 테스트 (Service) - Service 관련된 Bean이 IoC에 등록되면장
// PostRespository => 가짜 객체로 만들 수 있음 - Mockito 환경에서 이를 제공
@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTest {

    @InjectMocks // (PostService 객체가 만들어질때) 해당 파일에 @Mock로 등록된 모든 애들을 주입 받는다.
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void 저장하기_테스트() {

        // BODMocikto 방식
        // given
        Post post = new Post();
        post.setTitle("스프링부트 따라하기");
        post.setContent("스프링부트 따라하기 내용");

        // stub - 동작 지정
        when(postRepository.save(post)).thenReturn(post);

        // test execute
        Post postEntity = postService.저장하기(WritePostReq.builder()
                .title("스프링부트 따라하기")
                .content("스프링부트 따라하기 내용")
                .build());

        // then
        assertEquals(post, postEntity); // expected , actual
    }

    @Test
    public void 한건가져오기_테스트() {
        // given
        Long id = 1L;
        Post post = new Post();
        post.setId(id);
        post.setTitle("스프링부트 따라하기");
        post.setContent("스프링부트 따라하기 내용");

        // stub - 동작 지정
        when(postRepository.findById(id)).thenReturn(java.util.Optional.of(post));

        // when
        Post postEntity = postService.한건가져오기(id);

        // then
        assertEquals(post, postEntity);
    }

    @Test
    public void 모두가져오기_테스트() {
        // given
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(2L, "리액트 따라하기", "리액트 따라하기 내용"));

        // stub - 동작 지정
        when(postRepository.findAll()).thenReturn(postList);

        // when
        List<Post> postEntityList = postService.모두가져오기();

        // then
        assertEquals(postList, postEntityList);
    }

    @Test
    public void 수정하기_테스트() {
        // given
        Long id = 1L;

        Post post = new Post();
        post.setId(id);
        post.setTitle("스프링부트 따라하기");
        post.setContent("스프링부트 따라하기 내용");

        WritePostReq writePostReq = WritePostReq.builder()
                .title("스프링부트 또 따라하기")
                .content("스프링부트 또 따라하기 내용").build();

        // stub - 동작 지정
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));

        // when
        Post postEntity = postService.수정하기(id, writePostReq);

        // then
        assertEquals("스프링부트 또 따라하기", postEntity.getTitle());
        assertEquals("스프링부트 또 따라하기 내용", postEntity.getContent());

    }

    // 예외 처리 테스트?
    @Test
    public void 삭제하기_테스트() {
        // given
        Long id = 1L;

        // when

        // then
    }

}