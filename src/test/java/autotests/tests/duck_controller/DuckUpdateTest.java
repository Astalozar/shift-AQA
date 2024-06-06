package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckUpdateTest extends DuckActionsTest {
    @Test(description = "Проверить редактирование цвета и высоты уточки")
    @CitrusTest
    public void testEditDuckColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));
        createDuckTestData(runner, CheckEvenOdd.NoCheck, "yellow", "0.15", material, sound, wingsState);

        duckUpdate(runner, duckId(), "green", "0.95", material, sound, wingsState);

        validateResponse(runner, HttpStatus.OK,
                generateMessageJson("Duck with id = " + duckId() + " is updated"));
    }

    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        String height = "0.15";
        String material = "rubber";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));
        createDuckTestData(runner, CheckEvenOdd.NoCheck,  "yellow", height, material, "quack", wingsState);

        duckUpdate(runner, duckId(), "blue", height, material, "wuph", wingsState);

        validateResponse(runner, HttpStatus.OK,
                generateMessageJson("Duck with id = " + duckId() + " is updated"));
    }
}
