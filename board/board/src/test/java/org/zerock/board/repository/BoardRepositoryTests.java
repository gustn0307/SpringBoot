package org.zerock.board.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.board.entity.Board;
import org.zerock.board.entity.Member;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    //@Test
    public void insertBoard(){
        IntStream.rangeClosed(1,100).forEach(i->{
            Member member = Member.builder()
                    .email("user"+i+"@aaa.com").build();

            Board board = Board.builder()
                    .title("Title..."+i)
                    .content("Content...."+i)
                    .writer(member) // 어떤 회원이 게시글 작성자인지
                    .build();

            boardRepository.save(board);
        });
    }
    
    // @ManyToOne의 경우, FK쪽의 엔티티를 가져올 때 PK 쪽의 엔티티도 같이 가져옴
    // 하나의 메소드를 하나의 트랜잭션으로 처리하라는 의미, 트랜잭션으로 처리하면 속성에 따라 다르게 동작하지만 기본적으로 필요할 때 다시 데이터베이스와 연결 생성
    // 실행 결과를 보면 처음에는 board 테이블만을 로딩해서 처리하고 board.getWriter()를 처리하기 위해 member 테이블을 로딩함, 지연 로딩을 사용하는것보다 효율적
    @Transactional
    //@Test
    public void testRead1(){ // 쿼리가 내부적으로 lef outer join 처리됨

        Optional<Board> result = boardRepository.findById(100L); // 데이터베이스에 존재하는 번호
        
        Board board = result.get();
        
        System.out.println(board);

        // 이 부분에서 member 테이블을 로딩해야 하는데 이미 데이터베이스와의 연결은 끝난 상태이기 때문에 'no session' 메시지 발생
        // 이 문제를 해결하기 위해서는 다시 한번 데이터베이스와 연결이 필요한데 @Transactional이 이러한 처리에 도움을 줌
        System.out.println(board.getWriter());
    }

    // Lazy(지연 로딩)로 처리되었으나 실행되는 쿼리를 보면 조인 처리가 되어 한 번에 board 테이블과 member 테이블을 이용하는 것을 확인할 수 있음
    //@Test
    public void testReadWithWriter(){
        Object result = boardRepository.getBoardWithWriter(100L);

        Object[] arr = (Object[])result;

        System.out.println("-----------------------");
        System.out.println(Arrays.toString(arr));
    }

    // 중간에 'on'이 사용되면서 조인 조건을 직접 지정하는 부분이 추가되는 것을 볼 수 있음
    //@Test
    public void testGetBoardWithReply(){
        List<Object[]> result = boardRepository.getBoardWithReply(100L);

        for(Object[] arr : result){
            System.out.println(Arrays.toString(arr));
        }
    }

    //@Test
    public void testWithReplyCount(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Object[]> result = boardRepository.getBoardWithReplyCount(pageable);

        result.get().forEach(row->{
            Object[] arr = (Object[]) row;

            System.out.println(Arrays.toString(arr));
        });
    }

    //@Test
    public void testRead3(){
        Object result = boardRepository.getBoardByBno(100L);

        Object[] arr = (Object[]) result;

        System.out.println(Arrays.toString(arr));
    }

    //@Test
    public void testSearch1(){
        boardRepository.search1();
    }

    @Test
    public void testSearchPage(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending().and(Sort.by("title").ascending()));

        Page<Object[]> result = boardRepository.searchPage("t","1",pageable);
    }
}
