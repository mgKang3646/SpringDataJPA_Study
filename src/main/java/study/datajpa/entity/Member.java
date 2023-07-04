package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) //Reflection이 접근할수 있도록 PROTECTED로 설정
@ToString(of = {"id","username","age"} ) // team은 양방향관계이므로 toString 하지 않는다.
@NamedQuery(
        name="Member.findByUsername",
        query = "SELECT m FROM Member m WHERE m.username = :username"
)
@NamedEntityGraph(name = "Member.all",attributeNodes = @NamedAttributeNode("team"))
public class Member {
    @Id @GeneratedValue
    @Column(name ="member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
