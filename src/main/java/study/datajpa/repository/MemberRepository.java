package study.datajpa.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //파라미터가 두개까지는 메소드 이름으로 자동생성 사용, 넘어가면 JPQL로 풀기
    List<Member> findHelloBy(); //find...By 뒤에 아무것도 없으면 전체조회
    List<Member> findTop3HelloBy(); // 페이징을 간단히 구현, 짧은 쿼리를 간단히 메소드로 구현, 엔티티의 필드가 실수로 바뀌어도 메소드명과 달라 오류발견 가능

    // Named쿼리, @Query로 설정안해도 실행가능하다.
    // Named쿼리는 컴파일 시점에 JPQL을 파싱을 해서 어플리케이션 로딩시점에 JPQL의 오타를 잡을 수 있다.
    // 스프링 데이터 JPA는 Member 클래스의 네임드쿼리를 먼저 찾는다.
    // 네임드 쿼리가 없을때 메소드이름으로 쿼리 구현을 한다.
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username); // setParameter 설정 정보

    // 이름이 없는 Named쿼리이다.
    // JPQL을 파싱하기 때문에 로딩시점에 JPQL의 오타를 잡을 수 있다.
    // 복잡한 정적쿼리는 @Query로 해결하면 된다.
    // 복잡한 동적쿼리는 QueryDSL로 해결한다.
    @Query("SELECT m FROM Member m WHERE m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String usrename, @Param("age") int age);

    @Query("SELECT m.username FROM Member m ")
    List<String> findUsernameList();

    @Query("SELECT new study.datajpa.dto.MemberDto(m.id,m.username,t.name) FROM Member m JOIN m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩 ( IN절 )
    // 위치기반 파라미터 바인딩은 거의 사용X, 이름 기반 파라미터 바인딩 사용권장
    @Query("SELECT m FROM Member m WHERE m.username IN :names ")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 스프링 데이터 JPA는 반환타임을 굉장히 자유롭게 가져갈 수 있다.
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    //Page<Member> findByAge(int age, Pageable pageable);

    //페이징 쿼리와 카운트 쿼리를 분리할 수 있음
    //페이징 쿼리가 복잡해지면 카운트 쿼리도 따라서 복잡해져서 성능이 떨어질때가 있음
    //어차피 left join이면 카우트 쿼리를 분리하여 최적화시키면 성능을 올릴 수 있다. ( 자세히 다시 정리하기 )
    @Query(value = "SELECT m FROM Member m LEFT JOIN m.team t",countQuery = "SELECT COUNT(m.username) FROM Member m")
    Page<Member> findByAge(int age, PageRequest pageRequest);

    @Modifying(clearAutomatically = true) // 수정 쿼리 사용시 어노테이션을 추가해야 한다. clearAutomatically 자동으로 엔티티매니저 클리어시키기
    @Query("UPDATE Member m SET m.age = m.age+1 WHERE m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) // JOIN FETCH 를 어노테이션으로 사용하기
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) // 간단한 JOIN FETCH가 들어간 JPQL인 경우 어노테이션 사용하기, 복잡한 경우 JPQL 사용
    //@EntityGraph("Member.all") // Named 활용하기
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // 읽기 전용으로 만들어 스냅샷을 만들지 않아 성능이 최적화된다. ( 변경갑지를 안하는 경우, 변경감지에 소모되는 성능을 줄이기 위함 )
    // 그러나 굳이 이 설정을 넣는 수고를 들일정도로 효과적이지는 않다. 정말 필요한 경우에만 사용.
    @QueryHints(value = @QueryHint(name="org.hibernate.readOnly",value="true"))
    Member findReadOnlyByUsername(String username);

    //SELECT FOR UPDATE   LOCK 기능을 JPA에서 쉽게 제공하고 있다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
