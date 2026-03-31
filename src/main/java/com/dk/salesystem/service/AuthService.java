package com.dk.salesystem.service;

import com.dk.salesystem.dto.LoginRequest;
import com.dk.salesystem.dto.LoginResponse;
import com.dk.salesystem.entity.User;
import com.dk.salesystem.repository.UserRepository;
import com.dk.salesystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        if (user.getDeleted() == 1) {
            throw new UsernameNotFoundException("用户已被删除：" + username);
        }
        return user;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (DisabledException e) {
            throw new BadCredentialsException("用户已被禁用");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        }

        UserDetails userDetails = loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toList());

        List<String> permissions = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(jwtUtil.generateToken(userDetails))
                .expiresIn(jwtUtil.getExpiration())
                .userInfo(new LoginResponse.UserInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getNickname(),
                        user.getAvatar(),
                        user.getEmail(),
                        roles,
                        permissions
                ))
                .build();
    }

    public User register(User user, String rawPassword) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setStatus(1);
        user.setDeleted(0);
        return userRepository.save(user);
    }
}
