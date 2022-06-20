package sk.janobono.wci.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String... args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        System.out.println(passwordEncoder.encode("admin123"));
        System.out.println(passwordEncoder.encode("manager123"));
        System.out.println(passwordEncoder.encode("employee123"));
        System.out.println(passwordEncoder.encode("customer123"));
    }

}
