package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import autotests.payloads.Duck;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.CitrusParameters;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-controller")
@Feature("Эндпоинт /api/duck/create")
public class DuckCreateTest extends DuckActionsTest {
    @Test(description = "Проверить создание резиновой уточки")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        Duck duckProperties = new Duck()
                .color("yellow")
                .height(0.15)
                .material("rubber")
                .sound("quack")
                .wingsState(Duck.WingsState.FIXED);

        duckCreate(runner, duckProperties);

        // Пока у нас нет доступа к базе, id можно игнорировать, потому что он берётся из того же запроса,
        // который мы валидируем, и всегда будет корректным
        duckProperties.id("@ignore@");

        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
        validateDuckPropertiesInDatabase(runner,
                duckProperties.color(),
                String.valueOf(duckProperties.height()),
                duckProperties.material(),
                duckProperties.sound(),
                String.valueOf(duckProperties.wingsState()));
    }

    @Test(description = "Проверить создание деревянной уточки")
    @CitrusTest
    public void testCreateWoodenDuck(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        Duck duckProperties = new Duck()
                .color("yellow")
                .height(0.15)
                .material("wood")
                .sound("quack")
                .wingsState(Duck.WingsState.FIXED);

        duckCreate(runner, duckProperties);

        // В этом запросе id можно игнорировать, потому что он берётся из того же запроса,
        // который мы валидируем, и всегда будет корректным
        duckProperties.id("@ignore@");

        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
        validateDuckPropertiesInDatabase(runner,
                duckProperties.color(),
                String.valueOf(duckProperties.height()),
                duckProperties.material(),
                duckProperties.sound(),
                String.valueOf(duckProperties.wingsState()));
    }

    @DataProvider
    public Object[][] simpleDuckProvider() {
        return new Object[][]{
                {"yellow",   0.15,  "rubber",   "quack", Duck.WingsState.ACTIVE, null},
                {"желтая",  2.5,    "резиновая","кря",   Duck.WingsState.UNDEFINED, null}
                }; }

    @Test(description = "Параметризированное создание уточек", dataProvider = "simpleDuckProvider")
    @CitrusTest
    @CitrusParameters({"color", "height", "material", "sound", "wingsState", "runner"})

    public void testCreateParameterizedDucks(String color, double height, String material,
                                             String sound, Duck.WingsState wingsState,
                                             @Optional @CitrusResource TestCaseRunner runner) {
        Duck duckProperties = new Duck()
                .color(color)
                .height(height)
                .material(material)
                .sound(sound)
                .wingsState(wingsState);

        duckCreate(runner, duckProperties);

        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
        validateDuckPropertiesInDatabase(runner,
                duckProperties.color(),
                String.valueOf(duckProperties.height()),
                duckProperties.material(),
                duckProperties.sound(),
                String.valueOf(duckProperties.wingsState()));
    }


    Duck duckProperties1 = new
            Duck().color("green").height(1).material("wood").sound("QuAck").wingsState(Duck.WingsState.FIXED);
    Duck duckProperties2 = new
            Duck().color("blue").height(0).material("RuBBer").sound("mooo").wingsState(Duck.WingsState.ACTIVE);
    Duck duckProperties3 = new
            Duck().color("red").height(0.55).material("resin").sound("Hello").wingsState(Duck.WingsState.FIXED);

    @DataProvider
    public Object[][] payloadDuckProvider() {
        return new Object[][] {
                {duckProperties1, "duckActionCreateTest/duckGreenProperties.json", null},
                {duckProperties2, "duckActionCreateTest/duckBlueProperties.json", null},
                {duckProperties3, "duckActionCreateTest/duckRedProperties.json", null},
        }; }

    @Test(description = "Параметризированное создание уточек через payload и проверка через ресурсы",
            dataProvider = "payloadDuckProvider")
    @CitrusTest
    @CitrusParameters({"payload", "response", "runner"})
    public void testCreateParameterizedDucksWithPayload(Duck payload, String response,
                                             @Optional @CitrusResource TestCaseRunner runner) {

        duckCreate(runner, payload);

        receiveResponseAndValidateWithResource(runner, yellowDuckService, HttpStatus.OK, response,
                "$.id", duckIdVar);

        validateDuckPropertiesInDatabase(runner,
                payload.color(),
                String.valueOf(payload.height()),
                payload.material(),
                payload.sound(),
                String.valueOf(payload.wingsState()));
    }
}
