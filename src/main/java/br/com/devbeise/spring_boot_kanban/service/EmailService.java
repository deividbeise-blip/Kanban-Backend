package br.com.devbeise.spring_boot_kanban.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Seu código de verificação - Kanban");
        message.setText("Seu código de verificação é: " + code + "\n\nEle expira em 15 minutos.");
        mailSender.send(message);
    }
}