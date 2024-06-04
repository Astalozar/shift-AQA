package autotests.duck_controller;

import autotests.DuckTestUtils;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckCreateTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверить создание резиновой уточки")
    @CitrusTest
    public void testCreateRubberDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckCreateWithTestData(runner,
                "yellow", 0.15, "rubber", "quack", "FIXED");
    }

    @Test(description = "Проверить создание деревянной уточки")
    @CitrusTest
    public void testCreateWoodenDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckCreateWithTestData(runner,
                "yellow", 0.15, "wood", "quack", "FIXED");
    }

    public void duckCreateWithTestData(TestCaseRunner runner,
                                           String color, double height, String material, String sound, String wingsState) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        duckCreate(runner, color, height, material, sound, wingsState);

        String responseMessage = "{\n" +
                "  \"id\": " + DuckTestUtils.duckId() + ",\n" +
                "  \"color\": \"" + color + "\",\n" +
                "  \"height\": " + height + ",\n" +
                "  \"material\": \"" + material + "\",\n" +
                "  \"sound\": \"" + sound + "\",\n" +
                "  \"wingsState\": \"" + wingsState + "\"\n" +
                "}";

        runner.$(http().client(DuckTestUtils.getServerUrl())
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(fromBody().expression("$.id", DuckTestUtils.duckId()))
                .body(responseMessage));
    }

    public void duckCreate(TestCaseRunner runner,
                           String color, double height, String material, String sound, String wingsState ) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\n"
                        + "  \"color\": \"" + color + "\",\n"
                        + "  \"height\": " + height + ",\n"
                        + "  \"material\": \"" + material + "\",\n"
                        + "  \"sound\": \"" + sound + "\",\n"
                        + "  \"wingsState\": \"" + wingsState
                        + "\"\n" + "}"));
    }
}
