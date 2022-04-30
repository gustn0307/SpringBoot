package org.zerock.board.dto;

import lombok.*;

import java.time.LocalDateTime;

// Board 엔티티 클래스와 다른 점은 Member를 참조하는 대신에 화면에서 필요한 작성자의 이메일과 작성자의 이름으로 처리하고 있는점임
// 목록화면에서도 이용하기 떄문에 댓글 갯수도 추가
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    
    private Long bno;

    private String title;
    private String writerEmail; // 작성자의 이메일(id)
    private String writerName; // 작성자의 이름
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int replyCount; // 해당 게시글의 댓글 수
    

}
