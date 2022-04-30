package org.zerock.club.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Note extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 로그인한 사용자와 비교하기 위해 사용
    private ClubMember writer;

    // 수정이 가능한 부분은 제목과 내용이므로 이에 대한 수정이 가능하도록 메서드 구성
    public void changeTitle(String title){
        this.title=title;
    }

    public void changeContent(String content){
        this.content=content;
    }
}
