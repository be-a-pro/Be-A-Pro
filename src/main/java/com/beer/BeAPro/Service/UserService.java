package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public Long registerUser(AuthDto.SignupDto signupDto) {
        // password 암호화
        String encodedPassword = encoder.encode(signupDto.getPassword());
        AuthDto.SignupDto newSignupDto = AuthDto.SignupDto.encodePassword(signupDto, encodedPassword);

        User user = User.registerUser(newSignupDto);
        Long userId = userRepository.save(user).getId();

        return userId;
    }

    @Transactional
    public void saveRefreshToken(String email, String refreshToken) {
        User findUser = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NoSuchElementException("Can't find user with this email -> " + email);
        });

        User.saveRefreshToken(findUser, refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(String email) {
        User findUser = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NoSuchElementException("Can't find user with this email -> " + email);
        });

        User.deleteRefreshToken(findUser);
    }
}
