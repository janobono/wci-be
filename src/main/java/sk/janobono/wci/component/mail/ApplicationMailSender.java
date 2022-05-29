package sk.janobono.wci.component.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;

@Component
public class ApplicationMailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMailSender.class);

    private JavaMailSender javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(Mail mail) {
        LOGGER.debug("sendMail({})", mail);
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage,
                    mail.attachments() != null && !mail.attachments().isEmpty()
            );
            messageHelper.setFrom(mail.from());
            if (StringUtils.hasLength(mail.replyTo())) {
                messageHelper.setReplyTo(mail.replyTo());
            }
            mail.recipients().forEach(recipient -> {
                try {
                    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
            messageHelper.setSubject(mail.subject());
            messageHelper.setText(mail.content(), mail.html());
            if (mail.attachments() != null) {
                mail.attachments().forEach(attachment -> {
                    try {
                        messageHelper.addAttachment(attachment.getName(), attachment);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        javaMailSender.send(mimeMessagePreparator);
    }
}
