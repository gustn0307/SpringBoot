package org.zerock.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {
    @Autowired
    private GuestbookRepository guestbookRepository;

    //@Test
    public void insertDummies(){
        IntStream.rangeClosed(1,300).forEach(i->{
            Guestbook guestbook = Guestbook.builder()
                    .title("Title..."+i)
                    .content("Content..."+i)
                    .writer("user"+(i%10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    //@Test
    public void updateTest(){ // 수정 테스트
        Optional<Guestbook> result = guestbookRepository.findById(300L); // 존재하는 번호로 테스트

        if(result.isPresent()){
            Guestbook guestbook = result.get();

            guestbook.changeTitle("Changed Title.....");
            guestbook.changeContent("Changed Content.....");

            guestbookRepository.save(guestbook);
        }
    }

    //@Test
    public void testQuery1(){ // 단일 항목 검색 테스트
        Pageable pageable = PageRequest.of(0,10, Sort.by("gno").descending());

        // 동적으로 처리하기 위해 Q도메인 클래스를 얻어옴, Q도메인 클래스를 이용하면 엔티티 클래스에 선언된 title, content와 같은 필드들을 변수로 활용 가능
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword="1";

        // BooleanBuilder는 sql where문에 들어가는 조건들을 넣어주는 컨테이너라고 간주하면 됨
        BooleanBuilder builder = new BooleanBuilder(); 

        // 원하는 조건은 필드 값과 같이 결합해서 생성함. BooleanBuilder 안에 들어가는 값은 com.querydsl.core.types.Predicate 타입이어야 함(Java에 있는 타입과 혼동 주의)
        BooleanExpression expression = qGuestbook.title.contains(keyword); 

        // 만들어진 조건은 sql where문에 and나 or같은 키워드와 결합시킴
        builder.and(expression); 

        // BooleanBuilder는 GuestbookRepository에 추가된 QuerydslPredicateExcutor 인터페이스의 findAll()을 사용 가능
        Page<Guestbook> result = guestbookRepository.findAll(builder,pageable);
        
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }
    
    //@Test
    public void testQuery2(){ // 다중 항목 검색 테스트
        Pageable pageable = PageRequest.of(0,10, Sort.by("gno").descending());

        // 동적으로 처리하기 위해 Q도메인 클래스를 얻어옴, Q도메인 클래스를 이용하면 엔티티 클래스에 선언된 title, content와 같은 필드들을 변수로 활용 가능
        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword="1";

        // BooleanBuilder는 sql where문에 들어가는 조건들을 넣어주는 컨테이너라고 간주하면 됨
        BooleanBuilder builder = new BooleanBuilder();

        // 원하는 조건은 필드 값과 같이 결합해서 생성함. BooleanBuilder 안에 들어가는 값은 com.querydsl.core.types.Predicate 타입이어야 함(Java에 있는 타입과 혼동 주의)
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);

        BooleanExpression exContent = qGuestbook.content.contains(keyword);

        // exTitle과 exContent BooleanExpression을 or로 결합하는 부분
        BooleanExpression exAll = exTitle.or(exContent);

        // 만들어진 조건은 sql where문에 and나 or같은 키워드와 결합시킴, 결합된 BooleanExpression을 BooleanBuilder에 추가
        builder.and(exAll);

        // gno가 0보다 크다는 조건 추가
        builder.and(qGuestbook.gno.gt(0L));

        // BooleanBuilder는 GuestbookRepository에 추가된 QuerydslPredicateExcutor 인터페이스의 findAll()을 사용 가능
        Page<Guestbook> result = guestbookRepository.findAll(builder,pageable);

        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }
}
