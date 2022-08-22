package com.example.demo.web;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.dto.post.WritePostReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 통합 테스트(모든 Bean 들을 똑같이 IoC에 올리고 테스트 하는 것)
@Transactional // 각각의 테스트 함수가 종료될 때마다 Transaction 을 rollback 해줌 - 각각의 메서드의 독립적인 테스트를 보장
// 데이터는 삭제되지만, autoincrement 로 설정한 숫자 정책은 초기화 되지 않음
@AutoConfigureMockMvc // mock 을 메모리에 띄워 줌 (IoC에 등록해줌)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
/** WebEnvironment
 * WebEnvironment.MOCK (default)= 실제 톰켓으로 올리는 것이 아니라. 다른 톰캣으로 테스트 - 모의 웹 환경을 제공
 * WebEnvironment.RANDOM_PORT = 실제 톰켓으로 테스트 - 실제 웹 환경 제공
 */
// -> @ExtendWith(SpringExtension.class) - 스프링 환경 확장시 사용하는 애노테이션 - Spring 에서 JUnit5 에서 테스트 할때 필수
// @RunWith(SpringRunner.class) - Junit4 에서 테스트 할때 필수
public class PostControllerIntegreTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach // 모든 테스트 이전에 실행 됨
    public void init() {
        // 테이블 autoincrement 초기화

        // H2
        entityManager.createNativeQuery("ALTER TABLE post ALTER COLUMN id RESTART WITH 1").executeUpdate();
        // MySQL - entityManager.createNativeQuery("ALTER TABLE post AUTO_INCREMENT =1").executeUpdate();
    }

    @Test // 테스트 명시
    public void save_테스트() throws Exception {
        // given (테스트를 하기 위한 준비)
        WritePostReq writePostReq = WritePostReq.builder()
                .title("스프링 따라하기")
                .content("스프링 따라하기 내용")
                .build();
        // Object 를 JSON 으로 변경해주는 함수
        String content = new ObjectMapper().writeValueAsString(writePostReq);

        // 실제 postService 가 Bean 으로 등록 되어 있기 때문에, stub 이 필요 없음

        // when (테스트 실행)
        ResultActions resultAction = mockMvc.perform(post("/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then (검증)
        resultAction
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("스프링 따라하기")) // jsonPath : json 에서 변수로 결과 받아옴
                .andExpect(jsonPath("$.content").value("스프링 따라하기 내용")) // $ 는 전체를 뜻함, . 은 구분자
                .andDo(MockMvcResultHandlers.print()); // 결과 출력

    }

    @Test
    public void findAll_테스트() throws Exception{
        // given
        // data 생성
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(null, "리액트 따라하기", "리액트 따라하기 내요"));

        postRepository.saveAll(postList);

        // when
        ResultActions resultActions = mockMvc.perform(get("/post")
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].id").value(1L))
                .andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기")) // is 방법도 있지만 이는 harmcest 의 함수 여서 섞어 쓰면 헷갈림
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findById_테스트() throws Exception{
        // given
        // data 생성
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(null, "리액트 따라하기", "리액트 따라하기 내요"));

        postRepository.saveAll(postList);

        Long id = 1L;


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
        // data 생성
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(null, "리액트 따라하기", "리액트 따라하기 내요"));

        postRepository.saveAll(postList);

        Long id = 1L;
        WritePostReq writePostReq = WritePostReq.builder()
                .title("스프링부트 또 따라하기")
                .content("스프링부트 또 따라하기 내용")
                .build();
        String content = new ObjectMapper().writeValueAsString(writePostReq);

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
        // data 생성
        List<Post> postList = new ArrayList<>();
        postList.add(new Post(null, "스프링부트 따라하기", "스프링부트 따라하기 내용"));
        postList.add(new Post(null, "리액트 따라하기", "리액트 따라하기 내요"));

        postRepository.saveAll(postList);

        Long id = 1L;

        // when
        ResultActions resultAction = mockMvc.perform(delete("/post/{id}", id));

        // then
        resultAction.andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

        MvcResult requestResult = resultAction.andReturn();
        String result = requestResult.getResponse().getContentAsString();
        assertEquals("ok", result);
    }
}
