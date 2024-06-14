package autotests.clients;

import autotests.BaseTest;
import autotests.payloads.Duck;
import com.consol.citrus.TestCaseRunner;

import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.DelegatingPayloadVariableExtractor.Builder.fromBody;

public class DuckActionsTest extends BaseTest {
    /**
     * Определяет, требуется ли проверка на четность-нечетность id.
     */
    protected enum CheckEvenOdd {
        NoCheck,
        CheckEven,
        CheckOdd
    }

    protected static final String duckIdVar ="${duckId}";

    //region Работа с тестовыми данными
    @Step("Создать тестовую уточку")
    protected void createDuckTestData(TestCaseRunner runner, CheckEvenOdd checkEvenOdd,
                                          String color, String height, String material, String sound, String wingsState) {
        generateRandomFreeId(runner, checkEvenOdd);

        variable("color", color);
        variable("height", height);
        variable("material", material);
        variable("sound", sound);
        variable("wingsState", wingsState);

        sendDatabaseRequest(runner, yellowDuckDb,
                "INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                "VALUES(" + duckIdVar + ", '${color}', ${height}, '${material}', '${sound}', '${wingsState}')");

    }

    @Step("Удалить тестовую уточку")
    protected void removeDuckTestData(TestCaseRunner runner) {
        sendDatabaseRequest(runner, yellowDuckDb, "DELETE FROM DUCK WHERE ID = " + duckIdVar);
    }

    protected void generateRandomFreeId(TestCaseRunner runner, CheckEvenOdd checkEvenOdd) {
        sendDatabaseQueryAndExtract(runner, yellowDuckDb,
                "SELECT MAX(ID) AS ID FROM DUCK",
                "ID", duckIdVar);

        run(context -> {
            String duckId = context.getVariable(duckIdVar);
            // Если база пуста, то считаем, что прошлый id = 0
            if(duckId == null || "NULL".equals(duckId)) {
                duckId = "0";
            }
            boolean removeBeforeInserting = false;

            long id = Long.parseLong(duckId);
            // Если база забита до лимита id, то считаем, что прошлый id = 0
            if(id == Long.MAX_VALUE) {
                id = (long) ((Math.random() * (Long.MAX_VALUE - 1)) + 1);
                removeBeforeInserting = true;
            }

            // Получаем следующий свободный id
            id++;

            // Меняем id, если он не соответствует требованиям по чётности
            if (checkEvenOdd == CheckEvenOdd.CheckOdd && id % 2 == 0
                    || checkEvenOdd == CheckEvenOdd.CheckEven && id % 2 == 1) {
                id++;
            }

            context.setVariable(duckIdVar, id);

            if(removeBeforeInserting) {
                duckDelete(runner, duckIdVar);
            }
        });
    }

    //endregion

    //region Endpoints
    @Step("Получить свойства уточки /api/duck/action/properties")
    protected void duckProperties(TestCaseRunner runner, String id) {
        sendGetRequest(runner, yellowDuckService, "/api/duck/action/properties" +
                generateQueryParametersString("id", id));
    }

    @Step("Полет уточки /api/duck/action/fly")
    protected void duckFly(TestCaseRunner runner, String id) {
        sendGetRequest(runner, yellowDuckService, "/api/duck/action/fly" +
                generateQueryParametersString("id", id));
    }

    @Step("Крякание уточки /api/duck/action/quack")
    protected void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        sendGetRequest(runner, yellowDuckService, "/api/duck/action/quack" +
                generateQueryParametersString(
                        "id", id,
                        "repetitionCount", repetitionCount,
                        "soundCount", soundCount
                ));
    }

    @Step("Плавание уточки /api/duck/action/swim")
    protected void duckSwim(TestCaseRunner runner, String id) {
        sendGetRequest(runner, yellowDuckService, "/api/duck/action/swim" +
                generateQueryParametersString("id", id));
    }

    @Step("Создание уточки /api/duck/create")
    protected void duckCreate(TestCaseRunner runner, Duck duck) {
        sendPostRequest(runner, yellowDuckService, "/api/duck/create",
                MediaType.APPLICATION_JSON_VALUE, duck);
    }

    @Step("Удаление уточки /api/duck/delete")
    protected void duckDelete(TestCaseRunner runner, String id) {
        sendDeleteRequest(runner, yellowDuckService, "/api/duck/delete" +
                generateQueryParametersString("id", id));
    }

    @Step("Изменение свойств уточки /api/duck/update")
    protected void duckUpdate(TestCaseRunner runner, String id,
                           String color, String height, String material, String sound, String wingsState ) {
        sendPutRequest(runner, yellowDuckService, "/api/duck/update" +
                generateQueryParametersString(
                        "color", color,
                        "height", height,
                        "id", id,
                        "material", material,
                        "sound", sound,
                        "wingsState", wingsState
                ), MediaType.APPLICATION_JSON_VALUE, null);
    }
    //endregion

    @Step("Валидация ответа по строке {responseMessage}")
    protected void validateResponse(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        receiveResponseAndValidate(runner, yellowDuckService, status, responseMessage, "$", "_");
    }

    @Step("Валидация ответа по payload")
    protected void validateResponseWithPayload(TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        receiveResponseAndValidateWithPayload(runner, yellowDuckService, status, expectedPayload, "$", "_");
    }

    @Step("Валидация ответа по ресурсу {expectedPayload}")
    protected void validateResponseWithResource(TestCaseRunner runner, HttpStatus status, String resourcePath) {
        receiveResponseAndValidateWithResource(runner, yellowDuckService, status, resourcePath, "$", "var");
    }

    @Step("Проверить свойства уточки в базе данных")
    protected void validateDuckPropertiesInDatabase(TestCaseRunner runner,
                                                    String color, String height, String material,
                                                    String sound, String wingsState) {
        runner.$(query(yellowDuckDb)
                .statement("select * from DUCK where ID = '${" + duckIdVar + "}'")
                .validate("COLOR", color)
                .validate("HEIGHT", height)
                .validate("MATERIAL", material)
                .validate("SOUND", sound)
                .validate("WINGS_STATE", wingsState)
        );
    }

    @Step("Проверить, удалена ли уточка из базы данных")
    protected void validateDuckDeletedFromDatabase(TestCaseRunner runner) {
        sendDatabaseQueryAndValidate(runner, yellowDuckDb,
                "select COUNT(ID) as FOUND from DUCK where ID = '${" + duckIdVar + "}'",
                "FOUND", "0");
    }

    @Step("Проверить успешность создания уточки и записать ее id в тестовую переменную ")
    protected void validateDuckCreation(TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        receiveResponseAndValidateWithPayload(runner, yellowDuckService, status, expectedPayload,
                "$.id", duckIdVar);
    }
}
