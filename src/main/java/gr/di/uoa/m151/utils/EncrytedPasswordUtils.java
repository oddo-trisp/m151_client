package gr.di.uoa.m151.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncrytedPasswordUtils {

    // Encryte Password with BCryptPasswordEncoder
    public static String encryptedPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public static void main(String[] args) {
        String password = "123";
        String encrytedPassword = encryptedPassword(password);

        System.out.println("Encryted Password: " + encrytedPassword);
    }

}
