package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckActionFlyTest extends DuckActionsTest {
    @Test(description = "Проверить полет уточки с активными крыльями")
    @CitrusTest
    public void testFlyActive(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");

        duckFly(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateMessageJson("I am flying"));
    }

    @Test(description = "Проверить полет уточки со связанными крыльями")
    @CitrusTest
    public void testFlyFixed(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckFly(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateMessageJson("I can not fly"));
    }

    @Test(description = "Проверить полет уточки с неопределенными крыльями")
    @CitrusTest
    public void testFlyUndefined(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "UNDEFINED");

        duckFly(runner, duckId());

        validateResponse(runner, HttpStatus.INTERNAL_SERVER_ERROR,
                generateMessageJson("Wings are not detected"));
    }
}
