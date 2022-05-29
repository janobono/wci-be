package sk.janobono.wci.component.mail;

public enum MailTemplate {

    BASE("MailBaseTemplate");

    private final String template;

    MailTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
