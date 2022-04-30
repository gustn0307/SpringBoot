package org.zerock.club.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.util.Date;

@Log4j2
public class JWTUtil {

    // JWT 문자열 자체를 알면 누구든 API를 사용할 수 있다는 문제가 생기므로 만료기간(expire)을 설정하고 Signature(secretKey)를 생성
    private String secretKey = "zerock12345678";

    // 1 month
    private long expire = 60 * 24 * 30;

    // JWT(JSON Web Token)을 생성하는 역할
    public String generateToken(String content) throws Exception{

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(expire).toInstant()))
//                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(1).toInstant()))
                .claim("sub", content)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
                .compact();
    }

    // 인코딩된 문자열에서 원하는 값을 추출하는 용도
    public String validateAndExtract(String tokenStr)throws Exception{

        String contentValue=null;

        try {
            DefaultJws defaultJws = (DefaultJws) Jwts.parser().
                    setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(tokenStr);

            log.info(defaultJws);

            log.info(defaultJws.getBody().getClass());

            DefaultClaims claims = (DefaultClaims) defaultJws.getBody();

            log.info("------------------------");

            contentValue=claims.getSubject();

        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            contentValue=null;
        }
        return contentValue;
    }
}
