package com.yakgurt.bokyakjigi.user.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
            //            channel.anyRequest().requiresSecure() // 모든 요청에 대해 HTTPS만 허용하도록 강제함(로컬 개발 시에는 주석처리, 안그럼 8443으로 리디렉션 처리됨) TODO : 배포시에는 주석 지워서 활성화시키기
            //    )
                .cors(Customizer.withDefaults()) //cors 활성화, 기본제공하는 CORS설정 그대로 사용
                .csrf(csrf -> csrf.disable()) // CSRF 방어 비활성화
                .authorizeHttpRequests(auth -> auth // 인증/인가 규칙 설정 부분
                        .requestMatchers("/", "/error", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()  // 해당 요청 url은 인증 없이 모두 접근 가능
                        .anyRequest().authenticated() ); // 나머지 모든 요청은 로그인(인증 필요)

        return http.build(); // 설정이 완료된 HttpSecurity 객체의 빌드메서드 리턴값 반환(필터체인 빌드 후 반환)
    }

    @Bean 
    public CorsConfigurationSource corsConfigurationSource() { // 리액트 연동 시 필요함. 리액트와 스프링부트가 도메인이 달라서 브라우저가 보안상 막는게 있는데 리엑트 서버 주소는 괜찮다고 하는 설정이 필요함
        CorsConfiguration config = new CorsConfiguration(); // CORS 정책을 정의할 CorsConfiguration 객체 생성
        config.setAllowedOrigins(List.of("http://localhost:3000")); // React dev 서버 주소 : 어떤 출처(origin)에서 오는 요청을 허용할지 설정 TODO : 운영서버 도메인으로 바꾸기
        // 여러 개 도메인일 경우에는 config.setAllowedOrigins(List.of("https://myapp.com", "https://admin.myapp.com")); 아규먼트로 더 나열하면 된다
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 허용할 HTTP 메서드 종류를 명시(GET, POST, PUT, DELETE, OPTIONS 모두 허용)
        // OPTIONS는 브라우저가 사전 요청(preflight) 시 보내는 메서드

        config.setAllowedHeaders(List.of("*"));// 요청 헤더 중 어떤 것들을 허용할지 설정 : 모든(*) 종류의 헤더를 허용한다고 설정(개발중) TODO : 운영 환경에서는 보안을 위해 허용할 헤더를 명확히 제한하기
        config.setAllowCredentials(true); //쿠키나 인증 정보가 포함된 요청을 허용할지 여부 (true: 허용), react에서 쿠키, 인증 정보를 포함하려면 반드시 true로 해야 함

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 실제 CORS 정책을 URL 패턴별로 적용할 수 있게 하는 소스 객체 생성
        //-> 소스 객체는 어떤 url경로에 어떤 cors정책을 쓸지 등록하는 곳(url경로별로 cors허용 범위를 다르게 설정할 수 있다)

        source.registerCorsConfiguration("/api/**", config);  // API 경로(/api/**)에만 CORS 정책 적용, /api로 시작하는 하위 전체 경로 커버

        return source; // 완성된 CORS 정책 소스 객체 리턴
        //-> 스프링 컨테이너에 빈으로 등록하여 스프링 시큐리티나 MVC가 이 설정을 사용할 수 있도록 함
    }

}
