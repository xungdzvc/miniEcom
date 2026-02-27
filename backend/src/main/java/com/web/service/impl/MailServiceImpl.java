/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

/**
 *
 * @author ZZ
 */
import com.web.service.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from}")
  private String from;

  @Value("${app.mail.fromName:}")
  private String fromName;

  @Async("mailExecutor")
  @Override
  public void sendText(String to, String subject, String text) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(text);

    mailSender.send(msg);
  }

  @Async("mailExecutor")
  @Override
  public void sendHtml(String to, String subject, String html) {
    try {
      MimeMessage mime = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);

      // set From có tên hiển thị
      if (fromName != null && !fromName.isBlank()) {
        helper.setFrom(new InternetAddress(from, fromName));
      } else {
        helper.setFrom(from);
      }

      mailSender.send(mime);
    } catch (Exception e) {
      throw new RuntimeException("Send mail failed: " + e.getMessage(), e);
    }
  }
}