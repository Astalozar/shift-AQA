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

public class DuckActionFlyTest extends TestNGCitrusSpringSupport {
    @Test(description = "????????? ????? ?????? ? ????????? ????????")
    @CitrusTest
    public void testFlyActive(@Optional @CitrusResource TestCaseRunner runner) {
        duckFlyWithTestData(runner,
                "yellow", 0.15, "wood", "quack", "ACTIVE",
                HttpStatus.OK,"{\n" +
                        "  \"message\": \"I am flying\",\n" +
                        "}");
    }

    @Test(description = "????????? ????? ?????? ?? ?????????? ????????")
    @CitrusTest
    public void testFlyFixed(@Optional @CitrusResource TestCaseRunner runner) {
        duckFlyWithTestData(runner,
                "yellow", 0.15, "wood", "quack", "FIXED",
                HttpStatus.OK,"{\n" +
                        "  \"message\": \"I can not fly\",\n" +
                        "}");
    }

    @Test(description = "????????? ????? ?????? ? ??????????????? ????????")
    @CitrusTest
    public void testFlyUndefined(@Optional @CitrusResource TestCaseRunner runner) {
        duckFlyWithTestData(runner,
                "yellow", 0.15, "wood", "quack", "UNDEFINED",
                HttpStatus.INTERNAL_SERVER_ERROR,"{\n" +
                        "  \"message\": \"I can not fly\",\n" +
                        "}");
    }

    public void duckFlyWithTestData(TestCaseRunner runner,
                                    String color, double height, String material, String sound, String wingsState,
                                    HttpStatus status, String expectedResponseMessage) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                color, height, material, sound, wingsState);

        duckFly(runner, DuckTestUtils.duckId());

        DuckTestUtils.validateResponse(runner, status,
                expectedResponseMessage);
    }

    public void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id));
    }
}
