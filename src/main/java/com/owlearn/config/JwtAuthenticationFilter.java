package com.owlearn.config;

import com.owlearn.repository.UserRepository;
import com.owlearn.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider , UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = Long.valueOf(jwtTokenProvider.getUserId(token));

            // 1. 엔티티 Optional 조회
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                // DB에 해당 ID의 사용자가 없는 경우
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("User not found or Invalid access token");
                return;
            }

            // 💡 수정 1: Optional에서 User 엔티티를 추출
            User userEntity = userOptional.get();

            // 💡 수정 2: UserDetails로 캐스팅 (User 엔티티가 UserDetails를 구현해야 함)
            UserDetails userDetails = (UserDetails) userEntity;

            // 인증 객체 생성 (Principal: UserDetails)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}