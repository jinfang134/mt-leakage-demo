package demo.mt.leakage;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {
    JwtUtils jwtUtils=new JwtUtils();


    @Test
    void getClaimByToken() {
        jwtUtils.setSecret("f4e2e52034348f86b67cde581c0f9eb5[www.renren.io]");
        Claims claims=jwtUtils.getClaimByToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxOSIsImlhdCI6MTYyMTc0NjUzMCwiZXhwIjoxNjIyMzUxMzMwfQ.bjtdkfTbfetD8mnMaE-TyJxxVQaNaYnqjrFg8r3kNjwxq5J6cKp64WK681IFohRraOAAMMd_vSP0YL3TAy-qyw");
        System.out.println(claims.getSubject());
    }
}