package com.example.demo.web;

import com.example.demo.domain.Post;
import com.example.demo.dto.post.WritePostReq;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 단위 테스트(Controller 관련 로직만 띄우기) - Filter, ControllerAdvice
@Slf4j
@WebMvcTest // -> @ExtendWith(SpringExtension.class) - 스프링 환경 확장시 사용하는 애노테이션 - Spring 에서 JUnit5 에서 테스트 할때 필수
// @RunWith(SpringRunner.class) - Junit4 에서 테스트 할때 필수
public class PostControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // IoC 환경에 해당 가짜 빈이 등록된다.
    private PostService postService;

    // BDDMockito 패턴 - given(), when(), then() 함수 지원 - Mockito 를 확장하여 만든 라이브러리
    @Test // 테스트 명시
    public void save_테스트() throws Exception {
        // given (테스트를 하기 위한 준비)
        WritePostReq writePostReq = WritePostReq.builder()
                .title("스프링 따라하기")
                .content("스프링 따라하기 내용")
                .build();
        // Object 를 JSON 으로 변경해주는 함수
        String content = new ObjectMapper().writeValueAsString(writePostReq);

        // stub (미리 행동을 지정함) - postService 는 가짜 이기 때문에 제대로 실행되지 않기 때문에 - controller 만 신경쓰기 때문에 가능
        when(postService.저장하기(writePostReq)).thenReturn(new Post(1L, "스프링 따라하기", "스프링 따라하기 내용"));

        // when (테스트 실행)
        ResultActions resultAction = mockMvc.perform(post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then (검증)
        resultAction
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("스프링 따라하기")) // jsonPath : json 에서 변수로 결과 받아옴
                .andExpect(jsonPath("$.content").value("스프링 따라하기 내용")) // $ 는 전체를 뜻함, . 은 구분자
                .andDo(MockMvcResultHandlers.print()); // 결과 출력

    }

    @Test
    public void findAll_테스트() throws Exception{
        // given
        // stub 생성
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(2L, "리액트 따라하기", "리액트 따라하기 내요"));

        when(postService.모두가져오기()).thenReturn(postList);

        // when
        ResultActions resultActions = mockMvc.perform(get("/post")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기")) // is 방법도 있지만 이는 harmcest 의 함수 여서 섞어 쓰면 헷갈림
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findById_테스트() throws Exception{
        // given
        // stub 생성
        Long id = 1L;

        when(postService.한건가져오기(1L)).thenReturn(new Post(1L, "스프링부트 따라하기", "스프링부트 따라하기 내용"));


        // when
        ResultActions resultAction = mockMvc.perform(get("/post/{id}", id)
                .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultAction
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("스프링부트 따라하기"))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void update_테스트() throws Exception {
        // given
        Long id = 1L;
        WritePostReq writePostReq = WritePostReq.builder()
                .title("스프링부트 또 따라하기")
                .content("스프링부트 또 따라하기 내용")
                .build();
        String content = new ObjectMapper().writeValueAsString(writePostReq);

        // postService 는 가짜로 올라가있는 것 이기 때문에 실제 실행되는 것은 아님
        when(postService.수정하기(id, writePostReq)).thenReturn(new Post(1L, "스프링부트 또 따라하기", "스프링부트 또 따라하기 내용"));

        // when
        ResultActions resultActions = mockMvc.perform(put("/post/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("스프링부트 또 따라하기"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete_테스트() throws Exception {
        // given
        Long id = 1L;

        // postService 는 가짜로 올라가있는 것 이기 때문에 실제 실행되는 것은 아님
        when(postService.삭제하기(id)).thenReturn("ok");

        // when
        ResultActions resultActions = mockMvc.perform(delete("/post/{id}", id)
                .accept(MediaType.TEXT_PLAIN));

        // then - JSON 응답 시
        resultActions
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // 문자(String) 응답 시
        MvcResult requestResult = resultActions.andReturn();
        String result = requestResult.getResponse().getContentAsString();
        assertEquals("ok", result);
    }
}
