package com.yy.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    //模板引擎对象
    @Autowired
    private TemplateEngine templateEngine;
    //发送邮件者的邮箱
    @Value("${spring.mail.username}")
    private String FROM;


    public void sendTextEmail(String toAddress, String subject, String msgBody) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM);
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(msgBody);
        mailSender.send(simpleMailMessage);
    }


    public boolean sendHtmlEmail(String toAddress, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(toAddress);
            helper.setFrom(FROM);
            helper.setText(htmlBody, true);
            helper.setSubject(subject);
            mailSender.send(message);
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }


    public boolean sendHtmlEmail(String toAddress, String subject, String htmlBody, String filePath) {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toAddress);
            helper.setFrom(FROM);
            helper.setText(htmlBody, true);
            helper.setSubject(subject);
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            helper.addAttachment(file.getName(), file);
            mailSender.send(message);
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

    public boolean sendHtmlEmail(String toAddress, String subject, String templatePath, Map<String, String> dataMap) {
        Context context = new Context();
        context.setVariables(dataMap);
        return sendHtmlEmail(toAddress, subject, templateEngine.process(templatePath, context));
    }

}
