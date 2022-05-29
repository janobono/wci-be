package sk.janobono.wci.component.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

@Component
public class ApplicationMailContentFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMailContentFormatter.class);

    private TemplateEngine templateEngine;

    @Autowired
    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String format(MailTemplate template, IContext context) {
        LOGGER.debug("format({},{})", template, context);
        return templateEngine.process(template.getTemplate(), context);
    }
}
