package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void registerUser(AuthDto.SignupDto signupDto) {
        User user = User.registerUser(signupDto);
        userRepository.save(user);
    }

}
