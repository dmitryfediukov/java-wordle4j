package ru.yandex.practicum;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter("wordle.log", StandardCharsets.UTF_8.name())) {
            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = loader.load("words_ru.txt");
                WordleGame wordleGame = new WordleGame(dictionary, log);

                System.out.println("Игра запущена, слово загадано. Попробуй отгадать слово!");
                play(wordleGame, dictionary, log);

            } catch (Exception e) {
                log.println("Неопределенная ошибка: " + e);
                e.printStackTrace(log);
            }
        } catch (Exception e) { // сюда попадём, если лог не создался
            System.out.println("Не удалось создать лог-файл: " + e);
            e.printStackTrace();
        }
    }

    private static void play(WordleGame game, WordleDictionary dictionary, PrintWriter log) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (!game.isGameOver()) {
                System.out.println();
                System.out.println("Осталось попыток: " + game.getStepsLeft());
                System.out.print("> ");

                String input = scanner.nextLine();

                // Вызов подсказки при Enter
                if (input.isBlank()) {
                    String suggestion = game.suggest();
                    if (suggestion == null) {
                        System.out.println("Подсказок нет (варианты закончились). Введите слово вручную.");
                        continue; // попытка не тратится
                    }

                    System.out.println("> " + suggestion);
                    String hint = game.makeMove(suggestion);
                    System.out.println("> " + hint);
                    continue;
                }

                // обычный ход игрока
                try {
                    String guess = dictionary.validateAndNormalizeUserWord(input);

                    System.out.println("> " + guess);
                    String hint = game.makeMove(guess);
                    System.out.println("> " + hint);

                } catch (InvalidWordFormatException | WordNotFoundInDictionaryException e) {
                    // игровые ошибки: ход НЕ тратим
                    System.out.println(e.getMessage());
                    log.println("Invalid input: '" + input + "' reason=" + e.getMessage());
                }
            }

            // Итог
            System.out.println();
            if (game.isWon()) {
                System.out.println("Победа!");
                log.println("Слово отгадано, победа");
            } else {
                System.out.println("Попытки закончились.");
                System.out.println("Загаданное слово: " + game.getAnswer());
                log.println("Попытки закончились.");
            }
        }
    }
}
