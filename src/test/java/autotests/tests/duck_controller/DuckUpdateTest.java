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
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckUpdateTest extends DuckActionsTest {
    @Test(description = "Проверить редактирование цвета и высоты уточки")
    @CitrusTest
    public void testEditDuckColorHeightProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        double height = 0.15;
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner, duckId())));
        createDuckTestData(runner, CheckEvenOdd.NoCheck, color, height, material, sound, wingsState);

        color = "green";
        height = 0.95;

        duckUpdate(runner, duckId(), color, height, material, sound, wingsState);

        String updateResponseMessage = generateMessageJson("Duck with id = " + duckId() + " is updated");
        validateResponse(runner, HttpStatus.OK, updateResponseMessage);

        duckProperties(runner, duckId());

        String propertiesResponseMessage = generateDuckJson(color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, propertiesResponseMessage);
    }

    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSoundProperties(@Optional @CitrusResource TestCaseRunner runner) {
        String color = "yellow";
        double height = 0.15;
        String material = "rubber";
        String sound = "quack";
        String wingsState = "FIXED";

        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner, duckId())));
        createDuckTestData(runner, CheckEvenOdd.NoCheck, color, height, material, sound, wingsState);

        color = "blue";
        sound = "wuph";

        duckUpdate(runner, duckId(), color, height, material, sound, wingsState);

        String updateResponseMessage = generateMessageJson("Duck with id = " + duckId() + " is updated");
        validateResponse(runner, HttpStatus.OK, updateResponseMessage);

        duckProperties(runner, duckId());

        String propertiesResponseMessage = generateDuckJson(color, height, material, sound, wingsState);
        validateResponse(runner, HttpStatus.OK, propertiesResponseMessage);
    }

    public void duckUpdate(TestCaseRunner runner, String id,
                           String color, double height, String material, String sound, String wingsState ) {
        runner.$(http().client(getServerUrl())
                .send()
                .put("/api/duck/update")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("color", color)
                .queryParam("height", Double.toString(height))
                .queryParam("id", id)
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", wingsState));
    }

    // Мне кажется, правильнее будет вынести этот метод в родительский класс и брать данные напрямую из базы,
    // поскольку сейчас эта проверка зависит от реализации метода properties и не может служить достоверным показателем
    // успешности редактирования.
    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(getServerUrl())
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }
}
