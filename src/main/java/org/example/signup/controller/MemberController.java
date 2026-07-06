package org.example.signup.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.signup.dto.LoginRequestDto;
import org.example.signup.dto.SignupRequestDto;
import org.example.signup.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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
}
