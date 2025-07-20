package com.yakgurt.bokyakjigi.user.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration //->스프링 컨테이너에서 생성하고 관리하는 설정 컴포넌트 라는 뜻.
@EnableMethodSecurity //-> //-> 각각의 컨트롤러 메서드에서 인증(로그인), 권한 설정을 하기 위해서 사용하는 애너테이션
public class SecurityConfig {

    //Spring Security 5 버전부터는 비밀번호를 반드시 암호화해서 처리해야 함
    //-> 암호화되지 않은 비밀번호로 로그인 시도하면 예외 발생
    @Bean // 스프링 시큐리티에서 사용할 비밀번호 암호화(PasswordEncoder) 빈 등록
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
