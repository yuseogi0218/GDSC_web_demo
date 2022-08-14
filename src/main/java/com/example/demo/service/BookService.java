package com.example.demo.service;

import com.example.demo.domain.Book;
import com.example.demo.domain.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor // final 이 붙은 변수 생성자 자동 생성 : DI 자동 수행
@Service // Bean 에 등록 : 기능 정의, Transaction 관리 가능 (즉, 다수의 repository 의 함수를 호출하여 전체 흐름을 관리한다.)
public class BookService {

    private final BookRepository bookRepository;

    @Transactional // 해당 함수 종료 시, commit 또는 Rollback 수행 (트랜잭션 관리)
    public Book 저장하기(Book book) {
        return bookRepository.save(book);
    }

    // update 시 정합성 유지 : 해당 서비스 함수 실행 동안, 다른 함수가 객체의 값을 변경하여도 해당 서비스 트랜잭션 내부의 값은 그대로 유지된다.
    @Transactional(readOnly = true) // JPA는 변경 감지라는 내부 기능 off (성능 감소 방지), update 시 정합성을 유지 | insert의 유령데이터현상(팬텀현상) 못막음
    public Book 한건가져오기(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id를 확인해주세요!!"));
    }

    @Transactional(readOnly = true)
    public List<Book> 모두가져오기() {
        return bookRepository.findAll();
    }

    @Transactional
    public Book 수정하기(Long id, Book book) {
        // dirty check  후 update
        Book bookEntity = bookRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("id를 확인해주세요!!")); // 영속화 (Book 오브젝트) : 스프링 메모리 공간(영속성 컨텍스트)에 해당 객체(인스턴스)를 보유하고 있음

        bookEntity.setTitle(book.getTitle());
        bookEntity.setAuthor(book.getAuthor());

        return bookEntity;
    } // 해당 서비스 함수 종료 시 => 트랜잭션 종료 시 => 영속화 되어있는 데이터를 DB로 갱신 (flush) => DB에 commit =====> 더티 체킹

    @Transactional
    public String 삭제하기(Long id) {
        bookRepository.deleteById(id); // 예외 처리는 나중에 관리
        return "ok";
    }

}
