package autotests.tests.duck_controller;

import autotests.clients.DuckActionsTest;
import autotests.payloads.Duck;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

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
                .wingsState("FIXED");

        duckCreate(runner, duckProperties);

        // Пока у нас нет доступа к базе, id можно игнорировать, потому что он берётся из того же запроса,
        // который мы валидируем, и всегда будет корректным
        duckProperties.id("@ignore@");

        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
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
                .wingsState("FIXED");

        duckCreate(runner, duckProperties);

        // Пока у нас нет доступа к базе, id можно игнорировать, потому что он берётся из того же запроса,
        // который мы валидируем, и всегда будет корректным
        duckProperties.id("@ignore@");

        validateDuckCreation(runner, HttpStatus.OK, duckProperties);
    }
}
