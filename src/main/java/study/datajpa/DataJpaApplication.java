package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing // Auditing에 필수!!
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean // 생성자 수정자.
	public AuditorAware<String> auditorProvider(){
		return ()-> Optional.of(UUID.randomUUID().toString()); // 랜덤으로 생성해서 반환하기, 실제로는 세션에서 가져와 사용하면 된다.
	}

}
