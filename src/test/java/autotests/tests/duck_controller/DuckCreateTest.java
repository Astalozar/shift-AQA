package autotests.tests.duck_controller;

import autotests.DuckActionsTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckCreateTest extends DuckActionsTest {
    @Test(description = "Проверить создание резиновой уточки")
    @CitrusTest
    public void testCreateRubberDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        double height = 0.15;
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner, duckId())));

        duckCreate(runner, color, height, material, sound, wingsState);

        String responseMessage = generateDuckJson(duckId(), color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    @Test(description = "Проверить создание деревянной уточки")
    @CitrusTest
    public void testCreateWoodenDuckProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        double height = 0.15;
        String material = "wood";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner, duckId())));

        duckCreate(runner, color, height, material, sound, wingsState);

        String responseMessage = generateDuckJson(duckId(), color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, responseMessage);
    }

    // Для тестирования запроса на создание не стал пользоваться родительским методом для создания тестовых данных, т.к. в
    // дальнейшем он может поменяться и не использовать запрос для добавления данных.
    public void duckCreate(TestCaseRunner runner,
                           String color, double height, String material, String sound, String wingsState ) {
        String payload = generateDuckJson(color, height, material, sound, wingsState);
        runner.$(http().client(getServerUrl())
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload));
    }

    // Переопределил здесь валидацию, чтобы можно было подгрузить id уточки в переменную, чтобы потом корректно удалить
    // В идеале для проверки данных нужен доступ к базе данных.
    @Override
    public void validateResponse(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        runner.$(http().client(getServerUrl())
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(fromBody().expression("$.id", duckId()))
                .body(responseMessage));
    }
}
