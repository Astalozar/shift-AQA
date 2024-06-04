package autotests.duck_controller;

import autotests.DuckTestUtils;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckDeleteTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверить удаление существующей уточки")
    @CitrusTest
    public void testDeleteExistingDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                "yellow", 0.15, "rubber", "quack", "FIXED");

        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", DuckTestUtils.duckId())
                .message());

        DuckTestUtils.validateResponse(runner, HttpStatus.OK, "{\n" +
                "  \"message\": \"Duck is deleted\",\n" +
                "}");

    }
}
