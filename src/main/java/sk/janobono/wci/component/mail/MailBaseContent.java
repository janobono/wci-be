package sk.janobono.wci.component.mail;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.util.Objects;

public record MailBaseContent(String title, String message, MailLink mailLink) {

    public IContext getContext() {
        Context context = new Context();
        context.setVariable("title", title());
        context.setVariable("message", message());
        context.setVariable("isLink", Objects.nonNull(mailLink()));
        context.setVariable("linkHref", Objects.nonNull(mailLink()) ? mailLink().href() : "");
        context.setVariable("linkText", Objects.nonNull(mailLink()) ? mailLink().text() : "");
        return context;
    }
}
