package com.yakgurt.bokyakjigi.user.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * 설정하는 이유 :
 * 1. Spring Data Redis Repository 스캔을 제한하지 않으면 "Redis Repository candidate" 경고가 뜨기 때문에,
 * - Spring Data Redis Repository 스캔을 제한해서 경고 제거
 *
 * 2. RedisTemplate은 기본적으로 Object 직렬화를 사용하지만,
 *  JSON 직렬화를 사용해야 Redis 데이터가 사람도 읽기 쉽고 다른 시스템에서도 호환되기 때문에.
 */
@Configuration // Spring이 이 클래스를 설정 클래스(Config)로 인식하게 함
@EnableRedisRepositories(
        // RedisRepository를 스캔하지 않을 패키지를 제한
        basePackages = "com.yakgurt.bokyakjigi.user.redis" // Redis용 Repository가 없으므로 경고 제거용
)
public class RedisConfig {

    /**
     * RedisTemplate 빈 설정
     * - 키는 문자열
     * - 값은 JSON 직렬화
     *  redis는 바이트 배열로 메모리에 저장, 자바는 Object 등 자바 객체, 프론트는 JSON
     *  그래서 직렬화 역직렬화가 필요함.
     *  직렬화 : 자바 객체 -> redis에 저장가능한 byte배열 형태로 변환, 예)DTO -> JSON ->  byte[]
     *  역직렬화 : redis에서 가져온 byte[]을 java객체로 변환, 예) byte[] -> JSON -> DTO
     * 사용 이유 :  1) Redis 서버와 연결된 상태에서 데이터를 Key-Value 형태로 쉽게 저장/조회 가능
     * 2) Key와 Value 직렬화 방식을 명시하면, 다른 시스템과 데이터 호환성 유지 가능
     * @param connectionFactory Redis 서버 연결 정보를 가진 팩토리
     * @return RedisTemplate<String, Object> Redis 데이터 접근 객체
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // redis는 키가 만드시 문자열이여야함, Object는 그대로 넣으면 직렬화 문제 발생 가능성이 있기 때문에 Serializer 설정이 필수
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);  // Redis 서버와 연결

        // Key를 문자열로 직렬화 //-> Key 타입 이미 String으로 해놔서 굳이 필요는 없음(명시적으로 그냥 넣어둠)
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value를 JSON으로 직렬화
        // Object 형태의 데이터를 그대로 넣으면, Java 전용 직렬화가 되어 다른 시스템이나 직접 조회가 어려움
        // GenericJackson2JsonRedisSerializer는 Jackson을 이용해 JSON으로 변환
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet(); // 설정 반영
        return template; // 이 RedisTemplate 빈을 다른 클래스에서 @Autowired로 주입해서 사용 가능
    }
}
