package org.zerock.club.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
public class ClubAuthMemberDTO extends User implements OAuth2User { // DTO 역할을 수행하는 동시에 스프링 시큐리티에서 인가/인증 작업에 사용 가능

    private String email;

    private String password;

    private String name;

    private boolean fromSocial;

    private Map<String, Object> attr;

    //OAuth2User는 Map 타입으로 모든 인증 결과를 attributes라는 이름으로 가지고 있기 떄문에 ClubAuthMember 역시 attr이라는 변수를 만들어줌
    public ClubAuthMemberDTO(String username,
                             String password,
                             boolean fromSocial,
                             Collection<?extends GrantedAuthority> authorities,
                             Map<String, Object> attr){

        this(username,password,fromSocial,authorities);
        this.attr=attr;
    }

    public ClubAuthMemberDTO(String username,
                             String password,
                             boolean fromSocial,
                             Collection<?extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.email = username;
        this.password = password;
        this.fromSocial = fromSocial;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }
}
