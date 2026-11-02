package br.com.devbeise.spring_boot_kanban.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
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

    // Assíncrono para a resposta de /forgot-password levar o mesmo tempo
    // exista ou não o e-mail (o envio SMTP denunciaria o cadastro pelo tempo).
    @Async
    public void sendPasswordResetLink(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Redefinição de senha - Kanban");
        message.setText("Recebemos um pedido para redefinir sua senha.\n\n"
                + "Acesse o link abaixo para criar uma nova senha:\n" + link
                + "\n\nO link expira em 30 minutos e só pode ser usado uma vez."
                + "\nSe você não fez esse pedido, ignore este e-mail.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Falha ao enviar e-mail de redefinição de senha", e);
        }
    }
}