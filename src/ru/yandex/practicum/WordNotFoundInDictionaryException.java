package ru.yandex.practicum;

/*
Формат слова подходит, но слова нет в словаре
 */

public class WordNotFoundInDictionaryException extends Exception {
    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}
