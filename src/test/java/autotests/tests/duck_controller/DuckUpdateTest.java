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
    @Test(description = "Проверить редактирование цвета и высоты уточки")
    @CitrusTest
    public void testEditDuckColorHeight(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        Duck defaultDuckProperties = new Duck()
                .color("yellow")
                .height(0.15)
                .material("rubber")
                .sound("quack")
                .wingsState(Duck.WingsState.ACTIVE);

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                defaultDuckProperties.color(),
                String.valueOf(defaultDuckProperties.height()),
                defaultDuckProperties.material(),
                defaultDuckProperties.sound(),
                String.valueOf(defaultDuckProperties.wingsState()));

        defaultDuckProperties
                .color("green")
                .height(0.95);

        duckUpdate(runner, duckIdVar,
                defaultDuckProperties.color(),
                String.valueOf(defaultDuckProperties.height()),
                defaultDuckProperties.material(),
                defaultDuckProperties.sound(),
                String.valueOf(defaultDuckProperties.wingsState()));

        validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"" + "message" + "\": \"Duck with id = " + duckIdVar + " is updated\"\n" +
                        "}");

        validateDuckPropertiesInDatabase(runner, defaultDuckProperties);
    }

    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSound(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        Duck defaultDuckProperties = new Duck()
                .color("yellow")
                .height(0.15)
                .material("rubber")
                .sound("quack")
                .wingsState(Duck.WingsState.ACTIVE);

        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                defaultDuckProperties.color(),
                String.valueOf(defaultDuckProperties.height()),
                defaultDuckProperties.material(),
                defaultDuckProperties.sound(),
                String.valueOf(defaultDuckProperties.wingsState()));

        defaultDuckProperties
                .color("blue")
                .sound("wuphf");

        duckUpdate(runner, duckIdVar,
                defaultDuckProperties.color(),
                String.valueOf(defaultDuckProperties.height()),
                defaultDuckProperties.material(),
                defaultDuckProperties.sound(),
                String.valueOf(defaultDuckProperties.wingsState()));

        validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"" + "message" + "\": \"Duck with id = " + duckIdVar + " is updated\"\n" +
                        "}");

        validateDuckPropertiesInDatabase(runner, defaultDuckProperties);
    }
}
