package com.yakgurt.bokyakjigi.user.config.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component // 스프링 빈으로 등록해서 의존성 주입 가능하게 함
@ConfigurationProperties(prefix = "jwt") // application.yml에서 jwt로 시작하는 설정을 바인딩(이 클래스의 필드에 자동으로 매핑해준다)
public class JwtProperties {
    private String issuer;
    private String secret;
    private int accessTokenExpirationMinutes;
    private int refreshTokenExpirationDays;
    private String header;
    private String prefix;
}