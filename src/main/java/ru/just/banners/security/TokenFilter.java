package ru.just.banners.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.just.banners.model.dao.UserRecord;
import ru.just.banners.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    public static final String KEY_HEADER = "X-Auth-Key";
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(KEY_HEADER);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserRecord user = userRepository.getUserByToken(token);
        if (user == null) {
            throw new UsernameNotFoundException("Токен не найден");
        }
        String authority = user.getIsAdmin() ? "ADMIN" : "USER";
        AuthenticationToken authenticationToken = new AuthenticationToken(token, user.getUserId(),
                List.of(new SimpleGrantedAuthority(authority)));
        authenticationToken.setAuthenticated(true);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }
}
