package org.zerock.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.board.entity.Board;
import org.zerock.board.repository.search.SearchBoardRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {

    // 한 개의 로우(object) 내에 Object[]로 나옴
    // Board를 사용하고 있지만 Member를 같이 조회해야 하는 상황인데 Board 클래스에는 Member와 연관관계를 맺고 있으므로 b.writer와 같은 형태로 사용
    // 내부에 있는 엔티티를 이용할 때는 'LEFT JOIN' 뒤에 'ON'을 이용하는 부분이 없음
    @Query("select b, w from Board b left join b.writer w where b.bno =:bno")
    Object getBoardWithWriter(@Param("bno") Long bno);

    // Reply 쪽이 @ManyToOne으로 참조하고 있으나 Board 입장에서는 Reply 객체들을 참조하고 있지 않기 때문에 연관관계가 없는 엔티티 조인 처리에는 'ON'을 이용
    @Query("SELECT b,r FROM Board b LEFT JOIN Reply r ON r.board = b WHERE b.bno = :bno")
    List<Object[]> getBoardWithReply(@Param("bno") Long bno);
    
    // 목록 화면에 필요한 데이터
    // board : 게시물 번호, 제목, 작성시간
    // member: 이름/이메일(작성자)
    // reply : 해당 게시글의 댓글 수
    @Query(value="select b, w, count(r) " +
            "from Board b " +
            "left join b.writer w " +
            "left join Reply r on r.board = b " +
            "group by b",
            countQuery = "select count(b) from Board b")
    Page<Object[]> getBoardWithReplyCount(Pageable pageable); // 목록 화면에 필요한 데이터 

    // 목록 처리와 유사하지만 특정한 게시물 번호를 사용하는 부분이 다름
    @Query("select b, w, count(r) " +
            "from Board b left join b.writer w " +
            "left outer join Reply r on r.board = b " +
            "where b.bno = :bno")
    Object getBoardByBno(@Param("bno") Long bno);

}
