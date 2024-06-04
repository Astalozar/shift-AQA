package autotests.duck_action_controller;

import autotests.DuckTestUtils;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionSwimTest  extends TestNGCitrusSpringSupport {
    @Test(description = "Проверить плавание существующей уточки")
    @CitrusTest
    public void testSwimExisting(@Optional @CitrusResource TestCaseRunner runner) {
        duckSwimWithTestData(runner,
                "yellow", 0.15, "wood", "quack", "ACTIVE",
                HttpStatus.OK,"{\n" +
                        "  \"message\": \"I'm swimming\",\n" +
                        "}");
    }

    @Test(description = "Проверить плавание несуществующей уточки")
    @CitrusTest
    public void testSwimNonExisting(@Optional @CitrusResource TestCaseRunner runner) {
        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                "yellow", 0.15, "wood", "quack", "ACTIVE");
        DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId());
        duckSwim(runner, DuckTestUtils.duckId());

        DuckTestUtils.validateResponse(runner, HttpStatus.NOT_FOUND,
                "{\n" +
                        "  \"timestamp\": \"@ignore@\",\n" +
                        "  \"status\": 404,\n" +
                        "  \"error\": \"Duck not found\",\n" +
                        "  \"message\": \"Duck with id = " + DuckTestUtils.duckId() + " is not found\",\n" +
                        "  \"path\": \"/api/duck/action/fly\"\n" +
                        "}");
    }

//    {
//        "timestamp": "2024-05-12T12:52:46.246+0000",
//            "status": 404,
//            "error": "Duck not found",
//            "message": "Duck with id = 99 is not found",
//            "path": "/api/duck/action/fly"
//    }

    public void duckSwimWithTestData(TestCaseRunner runner,
                                    String color, double height, String material, String sound, String wingsState,
                                    HttpStatus status, String expectedResponseMessage) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                color, height, material, sound, wingsState);

        duckSwim(runner, DuckTestUtils.duckId());

        DuckTestUtils.validateResponse(runner, status,
                expectedResponseMessage);
    }

    public void duckSwim(TestCaseRunner runner, String id) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id));
    }
}
