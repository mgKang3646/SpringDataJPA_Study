package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id ){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    //도메인 클래스 컨버터 사용 ( id를 파라미터로 받지만 컨버터가 동작해서 회원 엔티티를 '조회'하여 반환한다. )
    //id는 기본키로 id를 외부로 공개해서 사용하는 경우가 드물다.
    //트랜잭션이 없는 상태에서 조회가 이루어지기에 단순조회용으로만 사용해야 한다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member ){
        //Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members") // 다양한 요청 파라미터를 넣을 수 있다.
    public Page<MemberDto> list(@PageableDefault(size=5) Pageable pageable){ // 디폴트를 사용하면 우선권이 높다.
        // 페이지는 0인데 페이지 1부터 시작하기
/*     PageRequest request = PageRequest.of(1,2);
       return memberRepository.findAll(request).map(MemberDto::new);*/

        // yml 파일에 1부터 시작 가능하도록 설정해놓음 one-indexed-parameters: true
        // 주의! 페이지는 0번으로 인식하여 데이터가 안 맞음 (?) 무슨의미인지 잘 모르겟으니 다시 확인할 필요 있음
       return memberRepository.findAll(pageable).map(MemberDto::new);


    }
    @PostConstruct
    public void init(){
        for(int i =0; i<100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }
}
