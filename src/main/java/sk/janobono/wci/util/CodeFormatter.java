package sk.janobono.wci.util;

public class CodeFormatter {

    public String format(String prefix, Long l) {
        return format(prefix, 19, l);
    }

    public String format(String prefix, int length, long l) {
        return prefix + String.format("%0" + length + "d", l);
    }
}
