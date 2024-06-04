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

public class DuckUpdateTest extends TestNGCitrusSpringSupport {
    @Test(description = "Проверить редактирование цвета и высоты уточки")
    @CitrusTest
    public void testEditDuckColorHeightProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckUpdatedWithTestData(runner,
                "yellow", 0.15, "rubber", "quack", "FIXED",
                "green", 0.95, null, null, null);
    }

    @Test(description = "Проверить редактирование цвета и звука уточки")
    @CitrusTest
    public void testEditDuckColorSoundProperties(@Optional @CitrusResource TestCaseRunner runner) {
        duckUpdatedWithTestData(runner,
                "yellow", 0.15, "rubber", "quack", "FIXED",
                "blue", null, null, "wuph", null);
    }

    public void duckUpdatedWithTestData(TestCaseRunner runner,
                                       String color, double height, String material, String sound, String wingsState,
                                       String newColor, Double newHeight, String newMaterial, String newSound, String newWingsState) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(), color, height, material, sound, wingsState);
        newColor = newColor == null ? color : newColor;
        newHeight = newHeight == null ? height : newHeight;
        newMaterial = newMaterial == null ? material : newMaterial;
        newSound = newSound == null ? sound : newSound;
        newWingsState = newWingsState == null ? wingsState : newWingsState;

        duckUpdate(runner, DuckTestUtils.duckId(), newColor, newHeight, newMaterial, newSound, newWingsState);
        DuckTestUtils.validateResponse(runner, HttpStatus.OK, "{\n" +
                "  \"message\": \"Duck with id = " + DuckTestUtils.duckId() + " is updated\",\n" +
                "}");

        String propertiesResponseMessage = "{\n" +
                "  \"color\": \"" + newColor + "\",\n" +
                "  \"height\": " + newHeight + ",\n" +
                "  \"material\": \"" + newMaterial + "\",\n" +
                "  \"sound\": \"" + newSound + "\",\n" +
                "  \"wingsState\": \"" + newWingsState + "\"\n" +
                "}";

        duckProperties(runner, DuckTestUtils.duckId());
        DuckTestUtils.validateResponse(runner, HttpStatus.OK, propertiesResponseMessage);
    }

    public void duckUpdate(TestCaseRunner runner, String id,
                           String color, double height, String material, String sound, String wingsState ) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
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

    public void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }
}
