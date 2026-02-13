package gdg.challenge.poom.global.security.domain;

import gdg.challenge.poom.domain.member.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String memberId;
    private final String role;

    public CustomUserDetails(Member member){
        this.memberId = member.getId().toString();
        this.role = member.getRole().toString();
    }

    public Long getMemberId() {
        return Long.parseLong(memberId);
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
}
