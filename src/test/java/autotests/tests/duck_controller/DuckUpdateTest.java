package autotests.tests.duck_controller;

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

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/update")
public class DuckUpdateTest extends DuckActionsTest {

    @Step("Редактирование цвета и высоты уточки")
    @Test(description = "Проверить редактирование цвета и высоты уточки")
    @CitrusTest
    public void testEditDuckColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "rubber", "quack", "FIXED");

        duckUpdate(runner, duckIdVar,
                "green", "0.95", "rubber", "quack", "FIXED");

        validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"" + "message" + "\": \"Duck with id = " + duckIdVar + " is updated\"\n" +
                        "}");

        validateDuckProperties(runner, new Duck()
                .color("green")
                .height(0.95)
                .material("rubber")
                .sound("quack")
                .wingsState(Duck.WingsState.FIXED));
    }

    @Step("Редактирование цвета и звука уточки")
    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "rubber", "quack", "FIXED");

        duckUpdate(runner, duckIdVar,
                "blue", "0.15", "rubber", "wuph", "FIXED");

        validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"" + "message" + "\": \"Duck with id = " + duckIdVar + " is updated\"\n" +
                        "}");

        validateDuckProperties(runner, new Duck()
                .color("blue")
                .height(0.15)
                .material("rubber")
                .sound("wuph")
                .wingsState(Duck.WingsState.FIXED));
    }
}
