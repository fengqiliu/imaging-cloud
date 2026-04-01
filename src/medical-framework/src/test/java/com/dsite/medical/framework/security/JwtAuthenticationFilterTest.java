package com.dsite.medical.framework.security;

import com.dsite.medical.framework.web.domain.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtToken jwtToken;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtToken);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("无Token时应该继续过滤链，不设置认证信息")
    void shouldContinueFilterChainWithoutToken() throws ServletException, IOException {
        // Given: 请求中没有Token
        when(jwtToken.validateToken(null)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then: 应该继续过滤链，不设置认证
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("无效Token时应该继续过滤链，不设置认证信息")
    void shouldContinueFilterChainWithInvalidToken() throws ServletException, IOException {
        // Given: 无效的Token
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtToken.validateToken("invalid-token")).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("有效Token时应该从数据库加载用户角色，不使用硬编码的ROLE_USER")
    void shouldLoadRolesFromDatabaseNotHardcoded() throws ServletException, IOException {
        // Given: 有效的Token
        String token = "valid-token";
        String username = "testuser";
        Long userId = 100L;

        request.addHeader("Authorization", "Bearer " + token);
        when(jwtToken.validateToken(token)).thenReturn(true);
        when(jwtToken.getUsernameFromToken(token)).thenReturn(username);
        when(jwtToken.getUserIdFromToken(token)).thenReturn(userId);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then: 验证角色不是硬编码的ROLE_USER
        // 注意: 当前实现是硬编码ROLE_USER，这个测试会失败
        // 实现修复后，应该从数据库或UserDetailsService加载真实角色
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication, "认证信息不应为空");
        assertEquals(username, authentication.getName(), "用户名应该匹配");

        // 获取认证中使用的权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 验证不应该使用硬编码的ROLE_USER
        // 如果只返回ROLE_USER，说明仍然是硬编码实现
        boolean hasOnlyRoleUser = authorities.size() == 1 &&
                authorities.iterator().next().getAuthority().equals("ROLE_USER");

        assertFalse(hasOnlyRoleUser,
                "不应该使用硬编码的ROLE_USER，应该从数据库加载真实角色");
    }

    @Test
    @DisplayName("Token前缀为Bearer时应该正确解析")
    void shouldParseBearerTokenCorrectly() throws ServletException, IOException {
        // Given: 带有Bearer前缀的Token
        String token = "my-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtToken.validateToken(token)).thenReturn(true);
        when(jwtToken.getUsernameFromToken(token)).thenReturn("user1");
        when(jwtToken.getUserIdFromToken(token)).thenReturn(1L);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtToken).validateToken(token);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("请求头没有Authorization时应该继续过滤链")
    void shouldContinueFilterChainWithoutAuthHeader() throws ServletException, IOException {
        // Given: 没有Authorization头
        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtToken, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Authorization头不是Bearer前缀时应该继续过滤链")
    void shouldContinueFilterChainWithNonBearerAuthHeader() throws ServletException, IOException {
        // Given: Authorization头存在但不是Bearer类型
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtToken, never()).validateToken(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("有效Token认证后LoginUser应包含正确的用户信息")
    void shouldSetCorrectUserInfoInAuthentication() throws ServletException, IOException {
        // Given
        String token = "valid-token";
        String username = "admin";
        Long userId = 1L;

        request.addHeader("Authorization", "Bearer " + token);
        when(jwtToken.validateToken(token)).thenReturn(true);
        when(jwtToken.getUsernameFromToken(token)).thenReturn(username);
        when(jwtToken.getUserIdFromToken(token)).thenReturn(userId);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        assertEquals(userId, loginUser.getUserId());
        assertEquals(username, loginUser.getUsername());
    }
}
