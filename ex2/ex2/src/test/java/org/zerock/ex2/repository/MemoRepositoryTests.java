package org.zerock.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.ex2.entity.Memo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository;

    //@Test
    public void testClass(){ // 실제 객체 확인(스프링이 내부적으로 동적 프록시 방식으로 자동으로 생성하는 클래스)
        System.out.println(memoRepository.getClass().getName());
    }

    //@Test
    public void testInsertDummies(){ // 삽입 작업 테스트

        IntStream.rangeClosed(1,100).forEach(i->{
            Memo memo = Memo.builder().memoText("Sample..."+i).build(); // 객체 생성
            memoRepository.save(memo); // 삽입
        });
    }

    //@Test
    public void testSelect(){ // 조회 작업 테스트1 - findById()

        // 데이터베이스에 존재하는 mno
        Long mno = 100L;

        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("===================================");

        if(result.isPresent()){
            Memo memo = result.get();
            System.out.println(memo);
        }
    }

    @Transactional
    //@Test
    public void testSelect2(){ // 조회 작업 테스트2 - getById()

        // 데이터베이스에 존재하는 mno
        Long mno = 100L;

        Memo memo = memoRepository.getById(mno);

        System.out.println("===================================");

        System.out.println(memo);
    }

    //@Test
    public void testUpdate(){ // 수정 작업 테스트
        Memo memo = Memo.builder().mno(100L).memoText("Update Text").build();

        System.out.println(memoRepository.save(memo));
    }

    //@Test
    public void testDelete(){ // 삭제 작업 테스트
        Long mno=100L;

        memoRepository.deleteById(mno); // 리턴 타입 void
    }

    //@Test
    public void testPageDefault(){ // 페이징 처리

        // 1페이지 10개
        Pageable pageable = PageRequest.of(0,10);

        Page<Memo> result = memoRepository.findAll(pageable);

        System.out.println(result);
        
        System.out.println("----------------------------------");
        
        System.out.println("Total Pages: "+result.getTotalPages()); // 총 몇 페이지

        System.out.println("Total Count: "+result.getTotalElements()); // 전체 개수
        
        System.out.println("Page Number: "+result.getNumber()); // 현재 페이제 번호 0부터 시작
        
        System.out.println("Page Size: "+result.getSize()); // 페이지당 데이터 개수
        
        System.out.println("has next page? "+result.hasNext()); // 다음 페이지 존재 여부
        
        System.out.println("first page? "+result.isFirst()); // 시작 페이지(0) 여부

        System.out.println("----------------------------------");

        for(Memo memo : result.getContent()){
            System.out.println(memo);
        }
    }

    //@Test
    public void testSort(){ // 정렬 테스트
        Sort sort1=Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // and를 이용한 연결

        Pageable pageable = PageRequest.of(0,10,sortAll);

        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo->{
            System.out.println(memo);
        });
    }

    //@Test
    public void testQueryMethods(){
        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L,80L);

        for(Memo memo:list){
            System.out.println(memo);
        }
    }

    //@Test
    public void testQueryMethodWithPageable(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("mno").descending());
        Page<Memo> result = memoRepository.findByMnoBetween(10L,50L,pageable);
        result.get().forEach(memo->System.out.println(memo));
    }

    @Commit
    @Transactional
    @Test
    public void testDeleteQueryMethods(){ // 엔티티 객체를 하나씩 삭제해 비효율적이어서 deleteBy는 실제 개발에는 많이 사용하지 않음
        memoRepository.deleteMemoByMnoLessThan(10L); 
    }
}
