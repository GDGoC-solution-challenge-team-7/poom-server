package gdg.challenge.poom.global.security.filter;

import gdg.challenge.poom.domain.auth.service.query.RedisStorageQueryService;
import gdg.challenge.poom.domain.member.entity.Member;
import gdg.challenge.poom.domain.member.service.MemberQueryService;
import gdg.challenge.poom.global.security.constants.AuthenticationConstants;
import gdg.challenge.poom.global.security.domain.CustomUserDetails;
import gdg.challenge.poom.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberQueryService memberQueryService;
    private final RedisStorageQueryService redisStorageQueryService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[JWT] {} {}", request.getMethod(), request.getRequestURI());
        if (!resolvableToken(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.resolveToken(request);
        if (isValid(token)) {
            Long memberId = jwtUtil.getMemberId(token);
            Member member = memberQueryService.findById(memberId);
            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private boolean resolvableToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthenticationConstants.AUTH_HEADER);
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthenticationConstants.TOKEN_PREFIX);
    }

    private boolean isValid(String token){
        return jwtUtil.isValid(token) && jwtUtil.getMemberId(token) != null && !redisStorageQueryService.isBlackList(token);
    }
}
