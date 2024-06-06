package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckDeleteTest extends DuckActionsTest {
    @Test(description = "Проверить удаление существующей уточки")
    @CitrusTest
    public void testDeleteExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", "0.15", "rubber", "quack", "FIXED");

        duckDelete(runner, duckId());

        validateResponse(runner, HttpStatus.OK, generateMessageJson("Duck is deleted"));
    }
}
