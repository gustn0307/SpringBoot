package org.zerock.board.entity;

import lombok.*;

import javax.persistence.*;

// 엔티티 간에 연관관계를 지정하는 경우에는 항상 @ToString을 주의해야 함
// @ToString은 해당 클래스의 모든 멤버 변수를 출력하는데 다른 엔티티 객체도 출력되면 또 다른 엔티티의 @ToString이 호출되고 이 때 데이터베이스 연결이 필요하게 됨
// 이런 문제로 인해 연관관계가 있는 엔티티 클래스의 경우 @ToString()을 할 때는 습관적으로 exclude 속성을 사용하는 것이 좋음
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "writer") // @ToString은 항상 exclude
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 명시적으로 Lazy 로딩 지정
    private Member writer; // 연관 관계 지정(어떤 멤버가 작성자인지)

    public void changeTitle(String title){
        this.title=title;
    }

    public void changeContent(String content){
        this.content=content;
    }

}
