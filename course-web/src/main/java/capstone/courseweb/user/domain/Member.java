package capstone.courseweb.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member implements UserDetails {
    @Id
    @Column(nullable = false, unique = true)//@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String Id; //소셜id

    private String email;

    private String password; //UserDetails에 있어서 넣어야됨. 값은 null로 저장

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberProvider provider;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.memberRole.getAuthority()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    } //계쩡 만료 여부

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MemberRole {
        GUEST,
        USER;
        private static final String PREFIX = "ROLE_" ;
        public String getAuthority() {
            return PREFIX + this.name();
        }
    }

    public enum  MemberProvider {
        LOCAL,
        KAKAO,
        NAVER,
        GOOGLE
    }
}
