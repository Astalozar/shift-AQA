package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import autotests.payloads.Duck;
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
@Feature("Эндпоинт /api/duck/action/properties")
public class DuckActionPropertiesTest extends DuckActionsTest {
    @Step("Получение свойств деревянной уточки с целым четным ID")
    @Test(description = "Проверить получение свойств деревянной уточки с целым четным ID")
    @CitrusTest
    public void testEvenWoodProperties(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckProperties(runner, duckId());

        validateResponseWithPayload(runner, HttpStatus.OK, new Duck()
                .color("yellow")
                .height(0.15)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED"));
    }

    @Step("Получение свойств резиновой уточки с целым нечетным ID")
    @Test(description = "Проверить получение свойств резиновой уточки с целым нечетным ID")
    @CitrusTest
    public void testOddRubberProperties(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckProperties(runner, duckId());

        validateResponseWithPayload(runner, HttpStatus.OK, new Duck()
                .color("yellow")
                .height(0.15)
                .material("wood")
                .sound("quack")
                .wingsState("FIXED"));
    }
}
