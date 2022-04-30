package org.zerock.club.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.club.security.dto.ClubAuthMemberDTO;

@Controller
@Log4j2
@RequestMapping("/sample/")
public class SampleController {

    @PreAuthorize("permitAll()") // 모두 접근 허용 - permitAll()
    @GetMapping("/all")
    public void exAll(){
        log.info("exAll..........");
    }

    // @AuthenticationPrincipal은 별도의 캐스팅 작업 없이 직접 실제 ClubAuthMemberDTO 타입을 사용할 수 있어 좀 더 편하게 사용가능
    @GetMapping("/member")
    public void exMember(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMember){
        log.info("exMember..........");

        log.info("------------------------");
        log.info(clubAuthMember);
    }

    @PreAuthorize("hasRole('ADMIN')") // value로 문자열로 된 표현식(expression)을 넣음
    @GetMapping("/admin")
    public void exAdmin(){
        log.info("exAdmin..........");
    }

    @PreAuthorize("#clubAuthMember != null && #clubAuthMember.username eq \"user95@zerock.org\"")
    @GetMapping("/exOnly")
    public String exMemberOnly(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMember){
        log.info("exMemberOnly..............");
        log.info(clubAuthMember);

        return "/sample/admin";
    }
}
