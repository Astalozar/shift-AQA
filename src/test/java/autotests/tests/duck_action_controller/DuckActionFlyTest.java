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

public class DuckActionFlyTest extends DuckActionsTest {
    @Test(description = "Проверить полет уточки с активными крыльями")
    @CitrusTest
    public void testFlyActive(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", 0.15, "wood", "quack", "ACTIVE");

        duckFly(runner, duckId());

        String responseMessage = generateMessageJson("I am flying");
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    @Test(description = "Проверить полет уточки со связанными крыльями")
    @CitrusTest
    public void testFlyFixed(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", 0.15, "wood", "quack", "FIXED");

        duckFly(runner, duckId());

        String responseMessage = generateMessageJson("I can not fly");
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    @Test(description = "Проверить полет уточки с неопределенными крыльями")
    @CitrusTest
    public void testFlyUndefined(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", 0.15, "wood", "quack", "UNDEFINED");

        duckFly(runner, duckId());

        String responseMessage = generateMessageJson("Wings are not detected");
        validateResponse(runner, HttpStatus.INTERNAL_SERVER_ERROR, responseMessage);
    }

    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client(getServerUrl())
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id));
    }
}
