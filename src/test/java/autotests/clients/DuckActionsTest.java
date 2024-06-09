package autotests.clients;

import autotests.EndpointConfig;
import autotests.payloads.Duck;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.TestCaseRunner;

import com.consol.citrus.message.builder.ObjectMappingPayloadBuilder;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.DelegatingPayloadVariableExtractor.Builder.fromBody;

@ContextConfiguration(classes = {EndpointConfig.class})

public class DuckActionsTest extends TestNGCitrusSpringSupport {
    /**
     * Определяет, требуется ли проверка на четность-нечетность id.
     */
    protected enum CheckEvenOdd {
        NoCheck,
        CheckEven,
        CheckOdd
    }

    @Autowired
    private HttpClient yellowDuckService;

    @Autowired
    protected SingleConnectionDataSource yellowDuckDb;

    protected static final String duckIdVar ="${duckId}";


    //region Работа с тестовыми данными
    /**
     * Выбрать свободный id из базы и создать уточку .
     */
    protected void createDuckTestData(TestCaseRunner runner, CheckEvenOdd checkEvenOdd,
                                          String color, String height, String material, String sound, String wingsState) {
        generateRandomFreeId(runner, checkEvenOdd);

        runner.$(sql(yellowDuckDb)
                .statement(String.format("INSERT INTO DUCK (ID, COLOR, HEIGHT, MATERIAL, SOUND, WINGS_STATE)\n" +
                        "VALUES(%s, '%s', %s, '%s', '%s', '%s')",
                        duckIdVar, color, height, material, sound, wingsState)));
    }

    /**
     * Удалить тестовые данные уточки с id, хранящимся в указанной тестовой переменной.
     */
    protected void removeDuckTestData(TestCaseRunner runner) {
        runner.$(sql(yellowDuckDb)
                .statement("DELETE FROM DUCK WHERE ID = " + duckIdVar));
    }

    protected void generateRandomFreeId(TestCaseRunner runner, CheckEvenOdd checkEvenOdd) {
        //variable(duckIdVar, "0");
        runner.$(query(yellowDuckDb)
                .statement("SELECT MAX(ID) AS ID FROM DUCK ")
                .extract("ID", duckIdVar));

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
    protected void duckProperties(TestCaseRunner runner, String id) {
        runner.$(http().client(yellowDuckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", id));
    }

    protected void duckFly(TestCaseRunner runner, String id) {
        runner.$(http().client(yellowDuckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", id));
    }

    protected void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        runner.$(http().client(yellowDuckService)
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", repetitionCount)
                .queryParam("soundCount", soundCount));
    }

    protected void duckSwim(TestCaseRunner runner, String id) {
        runner.$(http().client(yellowDuckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", id));
    }

    protected void duckCreate(TestCaseRunner runner, Duck duck) {
        runner.$(http().client(yellowDuckService)
                .send()

                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ObjectMappingPayloadBuilder(
                        duck,
                        new ObjectMapper())));
    }

    protected void duckDelete(TestCaseRunner runner, String id) {
        runner.$(http().client(yellowDuckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", id)
                .message());
    }

    protected void duckUpdate(TestCaseRunner runner, String id,
                           String color, String height, String material, String sound, String wingsState ) {
        runner.$(http().client(yellowDuckService)
                .send()
                .put("/api/duck/update")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("color", color)
                .queryParam("height", height)
                .queryParam("id", id)
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", wingsState));
    }

    //endregion


    /**
     * Провести валидацию ответа по указанным коду ответа и телу сообщения.
     */
    protected void validateResponse(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        runner.$(http().client(yellowDuckService)
                .receive()
                .response(status)
                .message()
                .body(responseMessage));
    }

    protected void validateResponseWithPayload(TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        runner.$(http().client(yellowDuckService)
                .receive()
                .response(status)
                .message()
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper())));
    }

    protected void validateResponseWithResource(TestCaseRunner runner, HttpStatus status, String expectedPayload) {
        runner.$(http().client(yellowDuckService)
                .receive()
                .response(status)
                .message()
                .body(new ClassPathResource(expectedPayload)));
    }

    protected void validateDuckProperties(TestCaseRunner runner, Duck expectedPayload) {
        runner.$(query(yellowDuckDb).statement("select * from DUCK where ID = '${" + duckIdVar + "}'")
                .validate("COLOR", expectedPayload.color())
                .validate("HEIGHT", Double.toString(expectedPayload.height()))
                .validate("MATERIAL", expectedPayload.material())
                .validate("SOUND", expectedPayload.sound())
                .validate("WINGS_STATE", expectedPayload.wingsState().toString())
        );
    }
    /**
     * Провести валидацию ответа по указанным коду ответа и телу сообщения и занести id в тестовую переменную
     */
    protected void validateDuckCreation(TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        runner.$(http().client(yellowDuckService)
                .receive()
                .response(status)
                .message()
                .extract(fromBody().expression("$.id", duckIdVar))
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper())));
    }
}
