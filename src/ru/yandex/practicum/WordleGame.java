package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;
    private int stepsLeft;
    private final WordleDictionary dictionary;
    private PrintWriter log;
    private final List<Attempt> history = new ArrayList<>();
    private final Set<String> suggestedAlready = new HashSet<>();
    private final Random random = new Random();

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        stepsLeft = 6;
        answer = dictionary.randomWord();
        log.println("Игра сформирована. В игре загадано слово: " + answer);
    }

    public String makeMove(String guess) {
        if (isGameOver()) {
            throw new IllegalStateException("Игра уже завершена.");
        }
        if (guess == null || guess.length() != 5) {
            throw new IllegalArgumentException("Попытка должна быть из 5 символов.");
        }

        String hint = checkGuessWithAnswer(guess, answer);
        history.add(new Attempt(guess, hint));
        stepsLeft--;

        log.println("Ход: слово=" + guess + ", подсказка=" + hint + ", попыток осталось =" + stepsLeft);

        return hint;
    }

    public boolean isWon() {
        return !history.isEmpty() && history.get(history.size() - 1).guess.equals(answer);
    }

    public boolean isGameOver() {
        return isWon() || stepsLeft <= 0;
    }

    public static String checkGuessWithAnswer(String guess, String answer) {
        if (guess == null || answer == null || guess.length() != 5 || answer.length() != 5) {
            throw new IllegalArgumentException("Попытка/Ответ должны состоять из 5 символов.");
        }

        char[] result = new char[5];
        Map<Character, Integer> remaining = new HashMap<>();

        // 1 проход: фиксируем '+', считаем оставшиеся буквы ответа
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            char a = answer.charAt(i);

            if (g == a) {
                result[i] = '+';
            } else {
                remaining.merge(a, 1, Integer::sum);
            }
        }

        // 2 проход: для не '+' ставим '^' или '-'
        for (int i = 0; i < 5; i++) {
            if (result[i] == '+') {
                continue;
            }
            char g = guess.charAt(i);

            if (remaining.containsKey(g)) {
                result[i] = '^';
                Integer value = remaining.get(g);
                value--;
                remaining.put(g, value);
                if ((value) == 0) {
                    remaining.remove(g);
                }
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    private static final class Attempt {
        final String guess;
        final String hint;

        Attempt(String guess, String hint) {
            this.guess = guess;
            this.hint = hint;
        }
    }

    //метод для автоматического предложения слова вместо игрока
    public String suggest() {
        List<String> candidates = computeCandidates();
        if (candidates.isEmpty()) {
            log.println("Нет подходящих слов для предложения");
            return null;
        }

        // чтобы не выдавать постоянно одно и то же
        List<String> fresh = new ArrayList<>();
        for (String c : candidates) {
            // не предлагать то, что уже вводили
            if (wasGuessedBefore(c)) continue;
            fresh.add(c);
        }

        if (fresh.isEmpty()) {
            // если всё уже предлагали — разрешаем повтор, иначе можно зациклиться
            fresh = candidates;
        }

        String pick = fresh.get(random.nextInt(fresh.size()));

        log.println("Предложенное слово=" + pick + ", подходящих слов=" + candidates.size());
        return pick;
    }

    // подбираем подходящие слова для подсказки, исходя из истории, если она есть
    private List<String> computeCandidates() {
        List<String> all = dictionary.getWords();
        if (history.isEmpty()) {
            return all; // если история ходов пустая — можно любое слово из словаря
        }

        //прогоняем слова из словаря по истории ходов, сравнивая подсказки. Подходящие слова записываем в кандидаты
        List<String> candidates = new ArrayList<>();
        for (String candidate : all) {
            boolean matches = true;

            for (Attempt a : history) {
                String actualHint = checkGuessWithAnswer(a.guess, candidate);
                if (!actualHint.equals(a.hint)) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private boolean wasGuessedBefore(String word) {
        for (Attempt a : history) {
            if (a.guess.equals(word)) return true;
        }
        return false;
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public String getAnswer() {
        return answer;
    }
}
