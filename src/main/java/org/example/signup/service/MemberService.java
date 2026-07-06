package org.example.signup.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.signup.domain.Member;
import org.example.signup.domain.MemberRepository;
import org.example.signup.dto.LoginRequestDto;
import org.example.signup.dto.SignupRequestDto;
import org.example.signup.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequestDto requestDto) {

        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordConfirm = requestDto.getPasswordConfirm();

        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호 일치하지 않음");
        }

        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 아이디");
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        memberRepository.save(member);
    }

    public String login(LoginRequestDto requestDto) {

        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호");
        }

        return jwtUtil.createToken(member.getUsername());
    }
}
