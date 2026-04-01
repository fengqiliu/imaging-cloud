package com.dsite.medical.framework.security;

import com.dsite.medical.common.constant.Constants;
import com.dsite.medical.framework.web.domain.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtToken jwtToken;

    @Autowired(required = false)
    private IUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtToken jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token) && jwtToken.validateToken(token)) {
            String username = jwtToken.getUsernameFromToken(token);
            Long userId = jwtToken.getUserIdFromToken(token);

            // 创建认证对象
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);

            // 获取用户角色
            List<SimpleGrantedAuthority> authorities;
            if (userDetailsService != null) {
                // 从数据库加载用户角色
                List<String> roles = userDetailsService.getUserRoles(userId);
                authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                        .collect(Collectors.toList());
            } else {
                // 降级处理：如果服务未实现，使用默认角色
                authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER);
        if (StringUtils.hasText(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            return token.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }
}
