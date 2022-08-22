package com.example.demo.web;

import com.example.demo.domain.Post;
import com.example.demo.dto.post.WritePostReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 통합 테스트 하는 법
 * 단점 : 모든 빈이 다 로드되기 때문에 통합 테스트가 가능하지만 느림!!
 * 장점 : 실제 서비스와 가장 유사하게 테스트 가능.
 * 팁 : SpringBootTest(class= {PostController.class, PostService.class, PostRepository.class}) 이렇게 필요한 빈만 올릴 수 도 있음.
 */

// TestRestTemplate는 MSA할 때....
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 실제 내장 톰켓이 랜덤 포트로 올라온다.
public class PostControllerIRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static HttpHeaders headers;

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void save_테스트() throws Exception {
        // given
        String content = new ObjectMapper().writeValueAsString(WritePostReq.builder()
                .title("스프링부트 따라하기")
                .content("스프링부트 따라하기 내용").build());
        HttpEntity<String> request = new HttpEntity<String>(content, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange("/post", HttpMethod.POST, request, String.class);

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        String author = dc.read("$.content");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("스프링부트 따라하기 내용", author);
    }


    /**
     *  참고 1
     *  ResponseEntity<Post[]> response = restTemplate.getForEntity("/post",  Post[].class);
     *  System.out.println(response.getBody()[0]);
     *  참고 2
     *  new ObjectMapper().writeValueAsString(자바오브젝트); // json으로 변경
     *  new ObjectMapper().readValue(제이슨데이터, 자바오브젝트.class); // java object로 변경
     */
    @Test
    public void findAll_테스트() {
        // given

        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/post",  String.class);
        System.out.println(response.getBody());

        // then
        DocumentContext dc = JsonPath.parse(response.getBody());
        String author = dc.read("$.[0].content");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("스프링부트 따라하기 내용", author);
    }

}
