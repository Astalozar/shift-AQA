package autotests.tests.duck_action_controller;

import autotests.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionQuackTest extends DuckActionsTest {

    @Test(description = "Проверить крякание уточки с нечетным ID и корректным звуком")
    @CitrusTest
    public void testOddCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        int repetitionCount = 2;
        int soundCount = 3;

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckOdd,
                "yellow", 0.15, "wood", "quack", "FIXED");

        duckQuack(runner, duckId(), Integer.toString(repetitionCount), Integer.toString(soundCount));

        String expectedSound = getExpectedSoundResult("quack", repetitionCount, soundCount);

        String responseMessage = generateMessageJson("sound",
                "@equalsIgnoreCase(" + expectedSound + ")@");
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    @Test(description = "Проверить крякание уточки с четным ID и корректным звуком")
    @CitrusTest
    public void testEvenCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        int repetitionCount = 2;
        int soundCount = 3;

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                "yellow", 0.15, "wood", "quack", "FIXED");

        duckQuack(runner, duckId(), Integer.toString(repetitionCount), Integer.toString(soundCount));

        String expectedSound = getExpectedSoundResult("quack", repetitionCount, soundCount);

        String responseMessage = generateMessageJson("sound",
                "@equalsIgnoreCase(" + expectedSound + ")@");
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    private String getExpectedSoundResult(String sound, int repetitionCount, int soundCount) {
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
        return expectedSound.toString();
    }

    private void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        runner.$(http().client(getServerUrl())
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", repetitionCount)
                .queryParam("soundCount", soundCount));
    }
}
