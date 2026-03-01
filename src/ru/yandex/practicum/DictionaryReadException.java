package ru.yandex.practicum;

/*
Не удается читать словарь
 */

public class DictionaryReadException extends Exception {

    public DictionaryReadException(String message, Throwable cause) {
        super(message,cause);
    }
}
