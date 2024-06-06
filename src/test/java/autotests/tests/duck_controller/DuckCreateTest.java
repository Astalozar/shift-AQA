package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckCreateTest extends DuckActionsTest {
    @Test(description = "Проверить создание резиновой уточки")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        String height = "0.15";
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        duckCreate(runner, color, height, material, sound, wingsState);

        validateResponseAndRecordId(runner, HttpStatus.OK,
                generateDuckJson(duckId(), color, height, material, sound, wingsState));
    }

    @Test(description = "Проверить создание деревянной уточки")
    @CitrusTest
    public void testCreateWoodenDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        String height = "0.15";
        String material = "wood";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        duckCreate(runner, color, height, material, sound, wingsState);

        validateResponseAndRecordId(runner, HttpStatus.OK,
                generateDuckJson(duckId(), color, height, material, sound, wingsState));
    }
}
