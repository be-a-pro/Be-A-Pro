package com.beer.BeAPro.Security;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

        return new UserDetailsImpl(findUser);
    }
}
