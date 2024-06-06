package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckActionPropertiesTest extends DuckActionsTest {
    @Test(description = "Проверить получение свойств деревянной уточки с целым четным ID")
    @CitrusTest
    public void testEvenWoodProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        String height = "0.15";
        String material = "wood";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                color, height, material, sound, wingsState);

        duckProperties(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateDuckJson(color, height, material, sound, wingsState));
    }

    @Test(description = "Проверить получение свойств резиновой уточки с целым нечетным ID")
    @CitrusTest
    public void testOddRubberProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        String height = "0.15";
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckOdd,
                color, height, material, sound, wingsState);

        duckProperties(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateDuckJson(color, height, material, sound, wingsState));
    }
}
