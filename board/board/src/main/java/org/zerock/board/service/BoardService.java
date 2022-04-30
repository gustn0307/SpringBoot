package org.zerock.board.service;

import org.zerock.board.dto.BoardDTO;
import org.zerock.board.dto.PageRequestDTO;
import org.zerock.board.dto.PageResultDTO;
import org.zerock.board.entity.Board;
import org.zerock.board.entity.Member;

public interface BoardService {
    Long register(BoardDTO dto);

    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO); // 게시물의 목록 처리

    BoardDTO get(Long bno); // 게시물 조회 처리

    void removeWithReplies(Long bno); // 삭제 기능

    void modify(BoardDTO boardDTO);
    
    // DTO가 연관관계를 가진 Board 엔티티 객체와 Member 엔티티 객체를 구성해야 하므로 내부적으로 Member 엔티티를 처리하는 과정을 거쳐야함
    // 이 때 Member는 실제 데이터베이스에 있는 이메일 주소를 사용해야 함
    // 실제로 게시물을 등록하는 register()에서 사용함
    default Board dtoToEntity(BoardDTO dto){ 
        Member member = Member.builder().email(dto.getWriterEmail()).build();

        Board board = Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .build();
        return board;
    }

    // Board 엔티티 객체와 Member 엔티티 객체, 댓글의 수를 파라미터로 전달받도록 구성하고 이를 이용해 BoardDTO 객체를 생성할 수 있도록 처리
    default BoardDTO entityToDTO(Board board, Member member, Long replyCount){

        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerEmail(member.getEmail())
                .writerName(member.getName())
                .replyCount(replyCount.intValue()) // long으로 나오므로 int로 처리하도록
                .build();

        return boardDTO;
    }
}
