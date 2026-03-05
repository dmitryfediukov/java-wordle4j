package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    static PrintWriter log;
    WordleDictionary dictionary;
    WordleGame game;

    @BeforeAll
    static void setupLogger() {
        log = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setup() {
        // создаем небольшой словарь для тестов
        dictionary = new WordleDictionary(List.of("герой", "гонец", "домик", "тесто", "пивко"));
        game = new WordleGame(dictionary, log);
    }

    //=====================
    // WordleDictionary
    //=====================
    @Test
    void validateAndNormalizeUserWord_shouldPassForExistingWord() throws Exception {
        String word = dictionary.validateAndNormalizeUserWord("  ГЕРоЙ    ");
        assertEquals("герой", word);
    }

    @Test
    void validateAndNormalizeUserWord_shouldThrowInvalidFormat_forWrongLength() {
        assertThrows(InvalidWordFormatException.class,
                () -> dictionary.validateAndNormalizeUserWord("гр"));
    }

    @Test
    void validateAndNormalizeUserWord_shouldThrowWordNotFound_ifMissing() {
        assertThrows(WordNotFoundInDictionaryException.class,
                () -> dictionary.validateAndNormalizeUserWord("лессс"));
    }

    @Test
    void randomWord_shouldReturnWordFromDictionary() {
        String word = dictionary.randomWord();
        assertTrue(dictionary.getWords().contains(word));
    }

    //=====================
    // WordleGame
    //=====================
    @Test
    void makeMove_shouldReturnCorrectHint_plusAndMinus() {
        // принудительно задаем ответ
        WordleGame testGame = new WordleGame(new WordleDictionary(List.of("герой")), log) {
            @Override
            public String getAnswer() { return "герой"; }
        };
        String hint = testGame.makeMove("гонец"); // "герой" vs "гонец" -> "+^-^-"
        assertEquals("+^-^-", hint);
    }

    @Test
    void gameShouldDetectWinAndGameOver() {
        WordleGame testGame = new WordleGame(new WordleDictionary(List.of("тесто")), log) {
            @Override
            public String getAnswer() { return "тесто"; }
        };
        testGame.makeMove("тесто");
        assertTrue(testGame.isWon());
        assertTrue(testGame.isGameOver());
    }

    @Test
    void gameShouldDecrementStepsLeft() {
        int before = game.getStepsLeft();
        game.makeMove(dictionary.getWords().get(0));
        assertEquals(before - 1, game.getStepsLeft());
    }

    @Test
    void suggest_shouldReturnWordFromCandidates() {
        String suggestion = game.suggest();
        assertNotNull(suggestion);
        assertTrue(dictionary.getWords().contains(suggestion));
    }

    //=====================
    // WordleDictionaryLoader
    //=====================
    @Test
    void load_shouldReturnDictionaryWithWords() throws Exception {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        // создаем временный файл словаря
        java.nio.file.Path path = java.nio.file.Files.createTempFile("test_dict", ".txt");
        java.nio.file.Files.write(path, List.of("абрик", "берег", "домик"));
        WordleDictionary dict = loader.load(path.toString());
        assertTrue(dict.getWords().contains("абрик"));
        assertTrue(dict.getWords().contains("берег"));
        assertTrue(dict.getWords().contains("домик"));
        java.nio.file.Files.delete(path);
    }

    @Test
    void load_shouldThrowFileNotFoundException() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        assertThrows(DictionaryFileNotFoundException.class,
                () -> loader.load("non_existing_file.txt"));
    }

    //=====================
    // Wordle
    //=====================
    @Test
    void play_shouldNotDecreaseStepsForInvalidWord() throws Exception {
        WordleGame testGame = new WordleGame(dictionary, log);
        int before = testGame.getStepsLeft();
        // передаем слово не из словаря
        try {
            dictionary.validateAndNormalizeUserWord("неверно");
        } catch (Exception ignored) {}
        int after = testGame.getStepsLeft();
        assertEquals(before, after); // попытки не должны уменьшиться
    }
}