package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

public class DuckActionSwimTest  extends DuckActionsTest {
    @Test(description = "Проверить плавание существующей уточки")
    @CitrusTest
    public void testSwimExisting(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");

        duckSwim(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateMessageJson("I'm swimming"));
    }

    @Test(description = "Проверить плавание несуществующей уточки")
    @CitrusTest
    public void testSwimNonExisting(@Optional @CitrusResource TestCaseRunner runner) {
        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");
        removeDuckTestData(runner);
        duckSwim(runner, duckId());

        validateResponse(runner, HttpStatus.NOT_FOUND,
                "{\n" +
                        "  \"timestamp\": \"@ignore@\",\n" +
                        "  \"status\": 404,\n" +
                        "  \"error\": \"Duck not found\",\n" +
                        "  \"message\": \"Duck with id = " + duckId() + " is not found\",\n" +
                        "  \"path\": \"/api/duck/action/fly\"\n" +
                        "}");
    }
}
