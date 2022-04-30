package org.zerock.board.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.board.entity.Board;
import org.zerock.board.entity.Reply;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class ReplyRepositoryTests {

    @Autowired
    ReplyRepository replyRepository;

    //@Test
    public void insertReply(){

        IntStream.rangeClosed(1, 300).forEach(i->{
            long bno = (long)(Math.random() * 100) +1; // 1부터 100까지의 임의의 번호

            Board board = Board.builder().bno(bno).build();

            Reply reply = Reply.builder()
                    .text("Reply........."+i)
                    .board(board) // 어떤 게시글
                    .replyer("guest")
                    .build();

            replyRepository.save(reply);
        });
    }

    // reply 테이블, board 테이블, member 테이블까지 모두 조인으로 처리됨
    // 이와 같이 특정한 엔티티를 조회할 때 연관관계를 가진 모든 엔티티를 같이 로딩하는 것을 'Eager loading'이라고 함(일반적으로 즉시 로딩이라는 용어로 표현)
    // 즉시로딩은 한번에 연관관계가 있는 모든 엔티티를 가져온다는 장점이 있지만, 여러 연관관계를 맺고 있거나 복잡할 수록 조인으로 인한 성능저하를 피할 수 없음
    // JPA에서 연관관계의 데이터를 어떻게 가져올 것인가를 fetch라고 하는데 연관관계의 어노테이션의 속성으로 fetch 모드를 지정함
    // 불필요한 조인까지 처리해야 하는 경우가 많기 때문에 가능하면 사용하지 않고, 그와 반대되는 개념인 Lazy loading(지연 로딩)으로 처리
    // 지연로딩(Lazy)은 조인을 하지 않기 때문에 단순하게 하나의 테이블을 이용하는 경우에는 빠른 속도의 처리가 가능하다는 장점이 있지만
    // 필요한 순간에 쿼리를 실행해야 하기 때문에 연관관계가 복잡한 경우에는 여러 번의 쿼리가 실행된다는 단점이 있음
    //@Test
    public void readReply1(){

        // Optional<T>는 null이 올 수 있는 값을 감싸는 Wrapper 클래스로, 참조하더라도 NPE가 발생하지 않도록 도와준다.
        // Optional 클래스는 값이 null이더라도 바로 NPE가 발생하지 않으며, 클래스이기 때문에 각종 메소드를 제공해준다.
        Optional<Reply> result = replyRepository.findById(1L);

        Reply reply = result.get();

        System.out.println(reply);
        System.out.println(reply.getBoard());
    }

    @Test
    public void testListByBoard(){
        List<Reply> replyList = replyRepository.getRepliesByBoardOrderByRno(Board.builder().bno(97L).build());

        replyList.forEach(reply -> System.out.println(reply));
    }
}
