package com.beer.BeAPro.Security;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final UserService userService;

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication) // 인증 정보 추출
                .map(authentication -> {
                    String authorities = getAuthorities(authentication);
                    if (authorities.equals("ROLE_USER") || authorities.equals("ROLE_ADMIN")){
                        User findUser = userService.findByEmail(authentication.getName());
                        if (findUser != null) {
                            return findUser.getId();
                        }
                    }
                    log.debug("AuditorAware: User not found or anonymous.");
                    return null;
                });
    }

    // 권한 추출
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
