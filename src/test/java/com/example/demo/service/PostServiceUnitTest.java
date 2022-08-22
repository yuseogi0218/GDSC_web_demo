package com.example.demo.service;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.dto.post.WritePostReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        post.setTitle("게시글 제목");
        post.setContent("게시글 내용");

        // stub - 동작 지정
        when(postRepository.save(post)).thenReturn(post);

        // test execute
        Post postEntity = postService.저장하기(WritePostReq.builder()
                .title("게시글 제목")
                .content("게시글 내용")
                .build());

        // then
        assertEquals(postEntity, post);
    }
}
