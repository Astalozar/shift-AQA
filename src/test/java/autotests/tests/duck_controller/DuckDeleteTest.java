package autotests.tests.duck_controller;

import autotests.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckDeleteTest extends DuckActionsTest {
    @Test(description = "Проверить удаление существующей уточки")
    @CitrusTest
    public void testDeleteExistingDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        createDuckTestData(runner, CheckEvenOdd.NoCheck,
                "yellow", 0.15, "rubber", "quack", "FIXED");

        runner.$(http().client(getServerUrl())
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", duckId())
                .message());

        String responseMessage = generateMessageJson("Duck is deleted");
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }
}
