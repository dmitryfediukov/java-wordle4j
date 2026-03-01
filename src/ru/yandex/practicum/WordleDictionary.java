package ru.yandex.practicum;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private final Random random = new Random();

    public WordleDictionary(List<String> rawWords) {
        this.words = new ArrayList<>();


        for (String rawWord : rawWords) {
            String word = normalize(rawWord);
            if (isValidWord(word)) {
                words.add(word);
            }
        }
    }

    public static String normalize(String rawWord) {
        if (rawWord == null || rawWord.isBlank()) {
            return "";
        }

        return rawWord.trim().toLowerCase().replace('ё', 'е');
    }

    public static boolean isValidWord(String word) {
        if (word == null || word.length() != 5) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'а' || c > 'я') {
                return false;
            }
        }
        return true;
    }

    public String validateAndNormalizeUserWord(String userInput)
            throws InvalidWordFormatException, WordNotFoundInDictionaryException {

        String word = normalize(userInput);

        if (word.isEmpty()) {
            throw new InvalidWordFormatException("Введите слово из 5 букв.");
        }

        if (!isValidWord(word)) {
            throw new InvalidWordFormatException("Слово должно быть из 5 русских букв.");
        }

        if (!words.contains(word)) {
            throw new WordNotFoundInDictionaryException("Слово в словаре не найдено.");
        }

        return word;
    }

    public List<String> getWords() {
        return List.copyOf(words);
    }

    public String randomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Игровой словарь пуст.");
        }
        return words.get(random.nextInt(words.size()));
    }
}
