package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/delete")
public class DuckDeleteTest extends DuckActionsTest {
    @Step("Удаление существующей уточки")
    @Test(description = "Проверить удаление существующей уточки")
    @CitrusTest
    public void testDeleteExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "rubber", "quack", "FIXED");

        duckDelete(runner, duckIdVar);

        validateResponse(runner, HttpStatus.OK,
                "{\n" +
                "  \"" + "message" + "\": \"Duck is deleted\"\n" +
                "}");
    }
}
