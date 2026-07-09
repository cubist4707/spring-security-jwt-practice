package org.example.signup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationCode(String email) {
        String code = generateRandomCode();
        redisTemplate.opsForValue().set(email, code, 3, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[회원가입] 이메일 인증 번호 안내");
        message.setText("인증 번호는 [" + code + "] 입니다. 3분 안에 입력해주세요.");

        javaMailSender.send(message);
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get(email);

        if (savedCode != null && savedCode.equals(inputCode)) {
            redisTemplate.delete(email);
            redisTemplate.opsForValue().set("verified_email:" + email, "true", 30, TimeUnit.MINUTES);
            return true;
        }
        return false;
    }

    public String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
}
