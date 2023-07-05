package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' ' + target.age}") // 일단 엔티티를 다 가져온 다음에 어플리케이션에서 Projection을 하는 것 ( OPEN PROJECTION )
    String getUsername(); //@Value가 없으면 DB단계에서 username을 PROJECTION 해서 반환 ( CLOSE PROJECTION )
}
