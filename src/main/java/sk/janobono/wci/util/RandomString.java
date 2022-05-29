package sk.janobono.wci.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomString {

    private enum Type {
        NUMERIC(true, false, false),
        ALPHA(false, true, false),
        ALPHA_NUMERIC(true, true, false),
        ALPHA_NUMERIC_SPECIAL(true, true, true);

        final boolean useNumbers;
        final boolean useAlpha;
        final boolean useSpecial;

        Type(boolean useNumbers, boolean useAlpha, boolean useSpecial) {
            this.useNumbers = useNumbers;
            this.useAlpha = useAlpha;
            this.useSpecial = useSpecial;
        }
    }

    private static final Character[] NUMBERS = new Character[]{
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    private static final Character[] ALPHA = new Character[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final Character[] ALPHA_CAP = new Character[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final Character[] SPECIAL = new Character[]{
            '.', ':', '~', '!', '@', '#', '$', '%', '*', '_'
    };

    private Type type = Type.ALPHA_NUMERIC;

    private int minNumbers = 0;

    private int minAlpha = 0;

    private int minAlphaCap = 0;

    private int minSpecial = 0;

    private final SecureRandom rnd;

    private RandomString(SecureRandom rnd) {
        this.rnd = rnd;
    }

    public static RandomString INSTANCE() {
        return INSTANCE("SHA1PRNG");
    }

    public static RandomString INSTANCE(String algorithm) {
        try {
            return new RandomString(SecureRandom.getInstance(algorithm));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm use for randomizer not exist.");
        }
    }

    public RandomString numeric() {
        this.type = Type.NUMERIC;
        return this;
    }

    public RandomString alphabet() {
        return alphabet(0, 0);
    }

    public RandomString alphabet(int minChars, int minCapitals) {
        this.type = Type.ALPHA;
        this.minAlpha = minChars;
        this.minAlphaCap = minCapitals;
        return this;
    }

    public RandomString alphaNumeric() {
        return alphaNumeric(0, 0, 0);
    }

    public RandomString alphaNumeric(int minNumbers, int minChars, int minCapitals) {
        this.type = Type.ALPHA_NUMERIC;
        this.minNumbers = minNumbers;
        this.minAlpha = minChars;
        this.minAlphaCap = minCapitals;
        return this;
    }

    public RandomString alphaNumericWithSpecial() {
        return alphaNumericWithSpecial(0, 0, 0, 0);
    }

    public RandomString alphaNumericWithSpecial(int minNumbers, int minChars, int minCapitals, int minSpecial) {
        this.type = Type.ALPHA_NUMERIC_SPECIAL;
        this.minNumbers = minNumbers;
        this.minAlpha = minChars;
        this.minAlphaCap = minCapitals;
        this.minSpecial = minSpecial;
        return this;
    }

    public String generate(int length) {
        return generate(length, length);
    }

    public String generate(int minLength, int maxLength) {
        checkMinimalCharacters(minLength, maxLength);
        int totalLength = getTotalStringLength(minLength, maxLength);

        List<Character> result = new ArrayList<>();
        if (type.useAlpha) {
            result.addAll(randomize(minAlpha, ALPHA));
            result.addAll(randomize(minAlphaCap, ALPHA_CAP));
        }

        if (type.useNumbers) {
            result.addAll(randomize(minNumbers, NUMBERS));
        }

        if (type.useSpecial) {
            result.addAll(randomize(minSpecial, SPECIAL));
        }

        if (result.size() < totalLength) {
            result.addAll(randomize(totalLength - result.size(), charactersByType()));
        }

        Collections.shuffle(result, rnd);
        return result.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private void checkMinimalCharacters(int minLength, int maxLength) {
        if (maxLength < minLength) {
            throw new RuntimeException("Min length cannot be more then min length");
        }
        int totalMinimals = getTotalMinimals();
        if ((minLength == maxLength) && (totalMinimals > minLength)) {
            throw new RuntimeException("You define more minimal characters than total length of string");
        } else if (totalMinimals > minLength) {
            minLength = totalMinimals;
            if (maxLength < totalMinimals) {
                throw new RuntimeException("You define more minimal characters than max length of string");
            }
        }
    }

    private int getTotalMinimals() {
        int totalMinimals = 0;
        if (type.useAlpha) {
            totalMinimals += minAlpha + minAlphaCap;
        }
        if (type.useNumbers) {
            totalMinimals += minNumbers;
        }
        if (type.useSpecial) {
            totalMinimals += minSpecial;
        }
        return totalMinimals;
    }

    private int getTotalStringLength(int minLength, int maxLength) {
        int result;

        if (minLength == maxLength) {
            result = minLength;
        } else {
            result = rnd.nextInt(maxLength - minLength) + minLength;
        }

        return result;
    }

    private List<Character> randomize(int size, Character[] characters) {
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(characters[rnd.nextInt(characters.length)]);
        }
        return result;
    }

    private Character[] charactersByType() {
        List<Character> result = new ArrayList<>();
        if (type.useAlpha) {
            result.addAll(Arrays.asList(ALPHA));
            result.addAll(Arrays.asList(ALPHA_CAP));
        }
        if (type.useNumbers) {
            result.addAll(Arrays.asList(NUMBERS));
        }
        if (type.useSpecial) {
            result.addAll(Arrays.asList(SPECIAL));
        }
        return result.toArray(Character[]::new);
    }

}
