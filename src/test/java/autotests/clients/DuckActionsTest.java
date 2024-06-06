package autotests.clients;

import autotests.EndpointConfig;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.TestCaseRunner;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.DelegatingPayloadVariableExtractor.Builder.fromBody;

@ContextConfiguration(classes = {EndpointConfig.class})

// На данный момент не увидел смысла разбивать на два класса для duck_controller и duck_action_controller.
// Всем тестам необходимы методы для создания и удаления тестовых данных, а они сейчас реализованы
// на запросах post и delete.
// В дальнейшем, с выделением BaseTest и появлением возможности работать с базой данных напрямую
// разбиение будет более оправданным
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

    private static final String duckIdTestVariable ="${duckId}";
    /**
     * @return Тестовая переменная, в которой хранится id последней созданной тестовой уточки.
     */
    protected static String duckId() {
        return duckIdTestVariable;
    }

    //region Работа с тестовыми данными
    /**
     * Создать тестовую уточку с указанными параметрами и занести её id в тестовую переменную.
     */
    protected void createDuckTestData(TestCaseRunner runner, CheckEvenOdd checkEvenOdd,
                                          String color, String height, String material, String sound, String wingsState) {
        duckCreate(runner, color, height, material, sound, wingsState);
        validateResponseAndRecordId(runner, HttpStatus.OK, null);

        // Если созданная уточка не подходит по четности id, удалить её и создать новую
        if(checkEvenOdd != CheckEvenOdd.NoCheck) {
            try {
                AtomicInteger duckId = new AtomicInteger(-1);
                run(context -> duckId.set(Integer.parseInt(context.getVariable(duckId()))));

                if (checkEvenOdd == CheckEvenOdd.CheckEven && duckId.get() % 2 != 0 ||
                        checkEvenOdd == CheckEvenOdd.CheckOdd && duckId.get() % 2 == 0) {
                    removeDuckTestData(runner);
                    createDuckTestData(runner, checkEvenOdd, color, height, material, sound, wingsState);
                }
            }
            catch (Exception e) {
                System.out.println("Invalid duck id " + e.getMessage());
            }
        }
    }

    /**
     * Удалить тестовые данные уточки с id, хранящимся в указанной тестовой переменной.
     */
    protected void removeDuckTestData(TestCaseRunner runner) {
        duckDelete(runner, duckId());
        validateResponse(runner, HttpStatus.OK, null);
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

    protected void duckCreate(TestCaseRunner runner,
                           String color, String height, String material, String sound, String wingsState ) {
        String payload = generateDuckJson(color, height, material, sound, wingsState);
        runner.$(http().client(yellowDuckService)
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload));    }

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

    /**
     * Провести валидацию ответа по указанным коду ответа и телу сообщения и занести id в тестовую переменную
     */
    protected void validateResponseAndRecordId(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        runner.$(http().client(yellowDuckService)
                .receive()
                .response(status)
                .message()
                .extract(fromBody().expression("$.id", duckId()))
                .body(responseMessage));
    }


    // Временная замена payload-ам, чтобы было проще создавать json-ы

    protected String generateDuckJson(String color, String height, String material, String sound, String wingsState) {
        return "{\n" +
                "  \"color\": \"" + color + "\",\n" +
                "  \"height\": " + height + ",\n" +
                "  \"material\": \"" + material + "\",\n" +
                "  \"sound\": \"" + sound + "\",\n" +
                "  \"wingsState\": \"" + wingsState + "\"\n" +
                "}";
    }

    protected String generateDuckJson(String id, String color, String height, String material, String sound, String wingsState) {
        return "{\n" +
                "  \"id\": " + id + ",\n" +
                "  \"color\": \"" + color + "\",\n" +
                "  \"height\": " + height + ",\n" +
                "  \"material\": \"" + material + "\",\n" +
                "  \"sound\": \"" + sound + "\",\n" +
                "  \"wingsState\": \"" + wingsState + "\"\n" +
                "}";
    }

    protected String generateMessageJson(String messageText) {
        return generateMessageJson("message", messageText);
    }

    protected String generateMessageJson(String messageKey, String messageText) {
        return "{\n" +
                "  \"" + messageKey + "\": \"" + messageText + "\"\n" +
                "}";
    }
}
