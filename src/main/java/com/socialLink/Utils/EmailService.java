package com.socialLink.Utils;


import com.socialLink.Dtos.EmailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public String sendEmail(EmailBody emailBody){

        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(emailBody.getRecipient());
            simpleMailMessage.setText(emailBody.getMsgBody());
            simpleMailMessage.setSubject(emailBody.getSubject());

            javaMailSender.send(simpleMailMessage);
            return "Mail sent successfully";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
