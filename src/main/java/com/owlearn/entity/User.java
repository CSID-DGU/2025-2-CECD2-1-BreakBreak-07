package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false,updatable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String role;

    // --- UserDetails 구현 메서드 ---

    /**
     * 사용자의 권한 목록을 반환합니다.
     * JWT 필터에서 인증 객체(Authentication) 생성 시 사용됩니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 현재는 단일 권한("ROLE_USER")만 반환하도록 고정했습니다.
        return List.of(new SimpleGrantedAuthority(this.role != null ? this.role : "ROLE_USER"));
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환합니다. (true는 만료되지 않았음을 의미)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료되지 않음
    }

    /**
     * 계정이 잠겨있지 않은지 여부를 반환합니다. (true는 잠겨있지 않음을 의미)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠겨있지 않음
    }

    /**
     * 자격 증명(비밀번호)이 만료되지 않았는지 여부를 반환합니다. (true는 만료되지 않았음을 의미)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 만료되지 않음
    }

    /**
     * 계정이 활성화되어 있는지 여부를 반환합니다. (true는 활성화되었음을 의미)
     */
    @Override
    public boolean isEnabled() {
        return true; // 활성화됨
    }

    /**
     * Spring Security에서 사용할 사용자 ID를 반환합니다. (username 역할)
     * 이 메서드는 논리적 로그인 ID를 반환하도록 합니다.
     */
    @Override
    public String getUsername() {
        return this.userId;
    }
}
