package ru.yandex.practicum;

/*
Неверный формат слова:
 -пустая строка;
 -не 5 букв;
 -что то кроме русских букв
 */

public class InvalidWordFormatException extends Exception {

    public InvalidWordFormatException(String message) {
        super(message);
    }
}
