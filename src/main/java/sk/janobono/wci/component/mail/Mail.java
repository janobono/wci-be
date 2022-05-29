package sk.janobono.wci.component.mail;

import java.io.File;
import java.util.List;

public record Mail(
        String from,
        String replyTo,
        List<String> recipients,
        String subject,
        String content,
        boolean html,
        List<File> attachments
) {
}
