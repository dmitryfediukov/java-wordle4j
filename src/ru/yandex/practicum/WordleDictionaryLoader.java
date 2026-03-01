package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String filename)
            throws DictionaryFileNotFoundException, DictionaryReadException {

        List<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }

            log.println("Загружено " + words.size() + " слов из словаря " + filename);

            return new WordleDictionary(words);

        } catch (FileNotFoundException e) {
            throw new DictionaryFileNotFoundException("Файл словаря не найден: " + filename, e);
        } catch (IOException e) {
            throw new DictionaryReadException("Ошибка при чтении файла словаря: " + filename, e);
        }
    }
}
