package com.example.notification.cipher;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Учебный шифр.
 *
 * Буквы A-Z (регистр не учитывается): пусть s = число пробелов в тексте,
 * k = (s == 0 ? 5 : s), i = индекс буквы (A=0 ... Z=25).
 * Значение буквы = round_half_up( (i + k)^k / k ).
 *
 * Числа: подряд идущие цифры считаются одним числом. Число переводится в двоичную
 * запись, делится на начало (длина / 2, округление вниз) и конец; конец
 * переворачивается, части склеиваются и результат переводится обратно в десятичное.
 * В шифртексте перед таким числом стоит префикс 'd'. Ведущие нули теряются.
 *
 * Формат шифртекста: токены (буквы и числа), идущие подряд, разделяются символом '|'.
 * Пробелы и знаки препинания (печатные ASCII-символы, кроме букв, цифр и '|')
 * остаются на своих местах без изменений. Символ '|' в исходном тексте запрещён.
 * Регистр не восстанавливается: при расшифровке буквы всегда в верхнем регистре.
 * Это не настоящая криптография: ключ (число пробелов) виден в шифртексте.
 */
public final class SpaceShiftCipher {

    private static final int ALPHABET_SIZE = 26;
    private static final int DEFAULT_K = 5;
    private static final char NUMBER_PREFIX = 'd';
    private static final char SEPARATOR = '|';

    /** Значение для буквы с индексом i при коэффициенте k (округление .5 вверх). */
    static BigInteger value(int i, int k) {
        BigInteger bk = BigInteger.valueOf(k);
        BigInteger num = BigInteger.valueOf(i + k).pow(k);
        // floor(num / k + 1/2) = floor((2*num + k) / (2*k))
        return num.shiftLeft(1).add(bk).divide(bk.shiftLeft(1));
    }

    /**
     * Преобразование числа: бинарная запись, начало (длина / 2) остаётся,
     * конец переворачивается. Применённое дважды, возвращает исходное число.
     */
    static BigInteger transformNumber(BigInteger n) {
        String bits = n.toString(2);
        int half = bits.length() / 2;
        String end = new StringBuilder(bits.substring(half)).reverse().toString();
        return new BigInteger(bits.substring(0, half) + end, 2);
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /** Знак препинания: печатный ASCII, кроме букв, цифр и разделителя. */
    private static boolean isPunctuation(char ch) {
        return ch > ' ' && ch <= '~' && !isLetter(ch) && !isDigit(ch) && ch != SEPARATOR;
    }

    private static int kFor(String text) {
        int s = 0;
        for (int c = 0; c < text.length(); c++) {
            if (text.charAt(c) == ' ') s++;
        }
        return s == 0 ? DEFAULT_K : s;
    }

    public String encrypt(String plain) {
        if (plain == null) throw new IllegalArgumentException("Message must not be null");
        int k = kFor(plain);
        StringBuilder sb = new StringBuilder();
        boolean needSeparator = false; // предыдущий символ вывода был токеном
        int c = 0;
        while (c < plain.length()) {
            char ch = plain.charAt(c);
            if (ch == ' ' || isPunctuation(ch)) {
                sb.append(ch);
                needSeparator = false;
                c++;
            } else if (isLetter(ch)) {
                if (needSeparator) sb.append(SEPARATOR);
                sb.append(value(Character.toUpperCase(ch) - 'A', k));
                needSeparator = true;
                c++;
            } else if (isDigit(ch)) {
                int start = c;
                while (c < plain.length() && isDigit(plain.charAt(c))) c++;
                BigInteger number = new BigInteger(plain.substring(start, c));
                if (needSeparator) sb.append(SEPARATOR);
                sb.append(NUMBER_PREFIX).append(transformNumber(number));
                needSeparator = true;
            } else if (ch == SEPARATOR) {
                throw new IllegalArgumentException(
                        "Character '" + SEPARATOR + "' is reserved as the token separator");
            } else {
                throw new IllegalArgumentException("Unsupported character: '" + ch + "'");
            }
        }
        return sb.toString();
    }

    public String decrypt(String cipher) {
        if (cipher == null) throw new IllegalArgumentException("Message must not be null");
        int k = kFor(cipher);
        Map<BigInteger, Character> table = new HashMap<>();
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            table.put(value(i, k), (char) ('A' + i));
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int n = cipher.length();
        while (i < n) {
            char ch = cipher.charAt(i);
            if (ch == ' ' || isPunctuation(ch)) {
                sb.append(ch);
                i++;
            } else if (ch == NUMBER_PREFIX || isDigit(ch)) {
                boolean isNumber = ch == NUMBER_PREFIX;
                int tokenStart = i;
                if (isNumber) i++;
                int digitsStart = i;
                while (i < n && isDigit(cipher.charAt(i))) i++;
                if (digitsStart == i) {
                    throw new IllegalArgumentException("Invalid token at position " + tokenStart);
                }
                BigInteger value = new BigInteger(cipher.substring(digitsStart, i));
                if (isNumber) {
                    sb.append(transformNumber(value));
                } else {
                    Character letter = table.get(value);
                    if (letter == null) throw new IllegalArgumentException("Unknown value: " + value);
                    sb.append(letter);
                }
                if (i < n) {
                    char next = cipher.charAt(i);
                    if (next == SEPARATOR) {
                        i++;
                        if (i >= n || !(cipher.charAt(i) == NUMBER_PREFIX || isDigit(cipher.charAt(i)))) {
                            throw new IllegalArgumentException("Separator must be followed by a token");
                        }
                    } else if (next == NUMBER_PREFIX) {
                        throw new IllegalArgumentException("Missing separator before position " + i);
                    }
                }
            } else {
                throw new IllegalArgumentException("Unexpected character in ciphertext: '" + ch + "'");
            }
        }
        return sb.toString();
    }
}