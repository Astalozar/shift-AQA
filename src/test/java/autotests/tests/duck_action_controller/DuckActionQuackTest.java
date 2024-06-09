package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/quack")

public class DuckActionQuackTest extends DuckActionsTest {
    @Step("Крякание уточки с нечетным ID и корректным звуком")
    @Test(description = "Проверить крякание уточки с нечетным ID и корректным звуком")
    @CitrusTest
    public void testOddCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        int repetitionCount = 2;
        int soundCount = 3;

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckOdd,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckQuack(runner, duckId(), Integer.toString(repetitionCount), Integer.toString(soundCount));

        validateResponse(runner, HttpStatus.OK, generateMessageJson("sound",
                "@equalsIgnoreCase(" +
                        getExpectedSoundResult("quack", repetitionCount, soundCount) + ")@"));
    }

    @Step("Крякание уточки с четным ID и корректным звуком")
    @Test(description = "Проверить крякание уточки с четным ID и корректным звуком")
    @CitrusTest
    public void testEvenCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        int repetitionCount = 2;
        int soundCount = 3;

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckQuack(runner, duckId(), Integer.toString(repetitionCount), Integer.toString(soundCount));

        validateResponse(runner, HttpStatus.OK, generateMessageJson("sound",
                        getExpectedSoundResult("quack", repetitionCount, soundCount)));
    }

    private String getExpectedSoundResult(String sound, int repetitionCount, int soundCount) {
        if(repetitionCount <= 0 || soundCount <= 0) {
            return "";
        }

        StringBuilder expectedSound = new StringBuilder();
        for(int i = 0; i < repetitionCount; i++) {
            for (int j = 0; j < soundCount; j++) {
                expectedSound.append(sound);
                if(j < soundCount - 1) {
                    expectedSound.append("-");
                }
            }
            if(i < repetitionCount - 1) {
                expectedSound.append(", ");
            }
        }

        return "@equalsIgnoreCase('" + expectedSound + "')@";
    }
}
