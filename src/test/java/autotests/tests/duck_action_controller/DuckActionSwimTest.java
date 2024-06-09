package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import autotests.payloads.ResponseMessage;
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
@Feature("Эндпоинт /api/duck/action/swim")
public class DuckActionSwimTest  extends DuckActionsTest {
    @Step("Плавание существующей уточки")
    @Test(description = "Проверить плавание существующей уточки")
    @CitrusTest
    public void testSwimExisting(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");

        duckSwim(runner, duckIdVar);

        validateResponseWithResource(runner, HttpStatus.OK,
                "duckActionSwimTest/SwimSuccessResponse.json");
    }

    @Step("Плавание несуществующей уточки")
    @Test(description = "Проверить плавание несуществующей уточки")
    @CitrusTest
    public void testSwimNonExisting(@Optional @CitrusResource TestCaseRunner runner) {
        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");
        removeDuckTestData(runner);
        duckSwim(runner, duckIdVar);

        validateResponseWithPayload(runner, HttpStatus.NOT_FOUND, new ResponseMessage()
                .timestamp("@ignore")
                .status(500)
                .error("Internal Server Error")
                .message("Duck with id = " + duckIdVar + " is not found")
                .path("/api/duck/action/fly"));
    }
}
