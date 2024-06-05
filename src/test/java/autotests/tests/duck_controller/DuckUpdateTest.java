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
        String color = "yellow";
        double height = 0.15;
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));
        createDuckTestData(runner, CheckEvenOdd.NoCheck, color, height, material, sound, wingsState);

        color = "green";
        height = 0.95;

        duckUpdate(runner, duckId(), color, height, material, sound, wingsState);

        String updateResponseMessage = generateMessageJson("Duck with id = " + duckId() + " is updated");
        validateResponse(runner, HttpStatus.OK, updateResponseMessage);

        duckProperties(runner, duckId());

        String propertiesResponseMessage = generateDuckJson(color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, propertiesResponseMessage);
    }

    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        double height = 0.15;
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));
        createDuckTestData(runner, CheckEvenOdd.NoCheck, color, height, material, sound, wingsState);

        color = "blue";
        sound = "wuph";

        duckUpdate(runner, duckId(), color, height, material, sound, wingsState);

        String updateResponseMessage = generateMessageJson("Duck with id = " + duckId() + " is updated");
        validateResponse(runner, HttpStatus.OK, updateResponseMessage);

        duckProperties(runner, duckId());

        String propertiesResponseMessage = generateDuckJson(color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, propertiesResponseMessage);
    }
}
