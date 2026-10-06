package gdg.challenge.poom.domain.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CoupleCodeGenerator {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUMERIC = LETTERS + DIGITS;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        char[] code = new char[6];

        // 영문자와 숫자를 각각 최소 1개 보장
        code[0] = randomChar(LETTERS);
        code[1] = randomChar(DIGITS);

        // 나머지 4자리는 영문자 또는 숫자
        for (int i = 2; i < code.length; i++) {
            code[i] = randomChar(ALPHANUMERIC);
        }

        // 순서를 무작위로 섞기
        for (int i = code.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = code[i];
            code[i] = code[j];
            code[j] = temp;
        }

        return new String(code);
    }

    private char randomChar(String characters) {
        return characters.charAt(random.nextInt(characters.length()));
    }
}
