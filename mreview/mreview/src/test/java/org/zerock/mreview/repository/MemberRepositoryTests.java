package org.zerock.mreview.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mreview.entitiy.Member;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberRepositoryTests {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReviewRepository reviewRepository;
    
    //@Test
    public void insertMembers(){ // 회원 더미 데이터 추가
        IntStream.rangeClosed(1,100).forEach(i->{

            Member member = Member.builder()
                    .email("r"+i+"@zerock.org")
                    .pw("1111")
                    .nickname("reviewer"+i)
                    .build();

            memberRepository.save(member);
        });
    }

    @Commit
    @Transactional // 다른 엔티티 참조하므로 Transactional 선언 필요
    @Test
    public void testDeleteMember(){ // 회원 삭제 : 맵핑 테이블인 review 테이블에서 먼저 삭제하고 m_member 테이블에서 삭제
        Long mid=1L; // Member의 mid

        Member member = Member.builder().mid(mid).build();

        memberRepository.deleteById(mid);
        reviewRepository.deleteByMember(member);

    }
}
