package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.mreview.entitiy.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
