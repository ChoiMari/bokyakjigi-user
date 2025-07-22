package com.yakgurt.bokyakjigi.user.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration //->스프링 컨테이너에서 생성하고 관리하는 설정용 컴포넌트 라는 뜻.
@EnableMethodSecurity //-> 각각의 컨트롤러 메서드에서 인증(로그인), 권한 설정을 하기 위해서 사용하는 애너테이션, 컨트롤러 메서드별 권한 제어(@PreAuthorize 등)를 활성화
public class SecurityConfig {

    //Spring Security 5 버전부터는 비밀번호를 반드시 암호화해서 처리해야 함
    //-> 암호화되지 않은 비밀번호로 로그인 시도하면 예외 발생
    @Bean // 스프링 시큐리티에서 사용할 비밀번호 암호화(PasswordEncoder) 빈 등록
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
             //   .requiresChannel(channel -> // TODO: 곧 없어질 예정이라고 컴파일 경고함, 추후 대체 API 찾아서 적용 필요(deprecation)
            //            channel.anyRequest().requiresSecure() // 모든 요청에 대해 HTTPS만 허용하도록 강제함(로컬 개발 시에는 주석처리, 안그럼 8443으로 리디렉션 처리됨)
            //    )
                .csrf(csrf -> csrf.disable()) // CSRF 방어 비활성화
                .authorizeHttpRequests(auth -> auth // 인증/인가 규칙 설정 부분
                        .requestMatchers("/", "/error", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()  // 해당 요청 url은 인증 없이 모두 접근 가능
                        .anyRequest().authenticated() ); // 나머지 모든 요청은 로그인(인증 필요)

        return http.build(); // 설정이 완료된 HttpSecurity 객체의 빌드메서드 리턴값 반환(필터체인 빌드 후 반환)
    }

}
