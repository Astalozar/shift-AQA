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
@Feature("Эндпоинт /api/duck/action/fly")

public class DuckActionFlyTest extends DuckActionsTest {
    @Step("Полет уточки с активными крыльями")
    @Test(description = "Проверить полет уточки с активными крыльями")
    @CitrusTest
    public void testFlyActive(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "ACTIVE");

        duckFly(runner, duckId());

        validateResponseWithResource(runner, HttpStatus.OK, "duckActionFlyTest/FlySuccessResponse.json");
    }

    @Step("Полет уточки со связанными крыльями")
    @Test(description = "Проверить полет уточки со связанными крыльями")
    @CitrusTest
    public void testFlyFixed(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckFly(runner, duckId());

        validateResponseWithPayload(runner, HttpStatus.OK, new ResponseMessage().message("I can not fly"));
    }

    @Step("Полет уточки с неопределенными крыльями")
    @Test(description = "Проверить полет уточки с неопределенными крыльями")
    @CitrusTest
    public void testFlyUndefined(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckFly(runner, duckId());

        validateResponseWithPayload(runner, HttpStatus.OK, new ResponseMessage().message("Wings not detected"));
    }
}
