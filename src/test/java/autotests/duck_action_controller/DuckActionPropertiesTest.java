package autotests.duck_action_controller;

import autotests.DuckTestUtils;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.actions.EchoAction.Builder.echo;
import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionPropertiesTest extends TestNGCitrusSpringSupport{
    @Test(description = "Проверить получение свойств деревянной уточки с целым четным ID")
    @CitrusTest
    public void testEvenWoodProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckPropertiesWithTestData(runner, DuckTestUtils.CheckEvenOdd.CheckEven,
                "yellow", 0.15, "wood", "quack", "FIXED");
    }

    @Test(description = "Проверить получение свойств резиновой уточки с целым нечетным ID")
    @CitrusTest
    public void testOddRubberProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckPropertiesWithTestData(runner, DuckTestUtils.CheckEvenOdd.CheckOdd,
                "yellow", 0.15, "rubber", "quack", "FIXED");
    }

    public void duckPropertiesWithTestData(TestCaseRunner runner, DuckTestUtils.CheckEvenOdd evenOdd,
                                           String color, double height, String material, String sound, String wingsState) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                color, height, material, sound, wingsState);
        if(evenOdd != DuckTestUtils.CheckEvenOdd.NoCheck) {
            run(context -> DuckTestUtils.recreateDuckTestData (
                    runner, context, evenOdd,
                    color, height, material, sound, wingsState));
        }

        duckProperties(runner, DuckTestUtils.duckId());

        DuckTestUtils.validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"color\": \"" + color + "\",\n" +
                        "  \"height\": " + height + ",\n" +
                        "  \"material\": \"" + material + "\",\n" +
                        "  \"sound\": \"" + sound + "\",\n" +
                        "  \"wingsState\": \"" + wingsState + "\"\n" +
                        "}");
    }

    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

}
