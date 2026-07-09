package org.example.signup.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.signup.dto.LoginRequestDto;
import org.example.signup.dto.SignupRequestDto;
import org.example.signup.service.EmailService;
import org.example.signup.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/api/member/signup")
    public void signup(@RequestBody SignupRequestDto requestDto) {
        memberService.signup(requestDto);
    }

    @PostMapping("/api/member/login")
    public String login(@RequestBody LoginRequestDto requestDto, HttpServletResponse res) {
        String token = memberService.login(requestDto);
        res.addHeader("Authorization", token);

        return "로그인 성공!";
    }

    @PostMapping("/api/member/logout")
    public String logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return memberService.logout(bearerToken);
    }

    @PostMapping("/api/member/email/send")
    public String sendEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.sendVerificationCode(email);
        return "인증번호가 발송되었습니다. 3분 안에 입력해주세요.";
    }

    @PostMapping("/api/member/email/verify")
    public String verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isVerified = emailService.verifyCode(email, code);

        if (isVerified) {
            return "이메일 인증에 성공했습니다.";
        }
        else {
            throw new IllegalArgumentException("인증번호가 틀렸거나 만료되었습니다.");
        }
    }
}
