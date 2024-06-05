package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.concurrent.atomic.AtomicInteger;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionsTest extends TestNGCitrusSpringSupport {

    /**
     * Определяет, требуется ли проверка на четность-нечетность id.
     */
    protected enum CheckEvenOdd {
        NoCheck,
        CheckEven,
        CheckOdd
    }

    private static final String serverUrl = "http://localhost:2222";
    protected String getServerUrl() {
        return serverUrl;
    }

    private static final String duckIdTestVariableName ="duckId";
    /**
     * @return имя тестовой переменной, в которой хранится id последней созданной тестовой уточки.
     */
    protected static String duckIdVarName() {
        return duckIdTestVariableName;
    }

    /**
     * @return форматированная тестовая переменная, в которой хранится id последней созданной тестовой уточки.
     */
    protected static String duckId() {
        return "${" + duckIdTestVariableName +"}";
    }

    /**
     * Создать тестовую уточку с указанными параметрами и занести её id в укаазанную тестовую переменную.
     */
    protected void createDuckTestData(TestCaseRunner runner, CheckEvenOdd checkEvenOdd,
                                          String color, double height, String material, String sound, String wingsState) {
        String payload = generateDuckJson(color, height, material, sound, wingsState);
        runner.$(http().client(getServerUrl())
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload));
        runner.$(http().client(getServerUrl())
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", duckIdVarName())));

        if(checkEvenOdd != CheckEvenOdd.NoCheck) {
            try {
                AtomicInteger duckId = new AtomicInteger(-1);
                run(context -> duckId.set(Integer.parseInt(context.getVariable(duckIdVarName()))));

                if(checkEvenOdd == CheckEvenOdd.CheckEven && duckId.get() % 2 != 0 ||
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
        runner.$(http().client(getServerUrl())
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", duckId())
                .message());
        runner.$(http().client(getServerUrl())
                .receive()
                .response(HttpStatus.OK)
                .message());
    }

    /**
     * Провести валидацию ответа по указанным коду ответа и телу сообщения.
     */
    protected void validateResponse(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        runner.$(http().client(getServerUrl())
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage));
    }

    protected String generateDuckJson(String color, double height, String material, String sound, String wingsState) {
        return "{\n" +
                "  \"color\": \"" + color + "\",\n" +
                "  \"height\": " + height + ",\n" +
                "  \"material\": \"" + material + "\",\n" +
                "  \"sound\": \"" + sound + "\",\n" +
                "  \"wingsState\": \"" + wingsState + "\"\n" +
                "}";
    }

    protected String generateDuckJson(String id, String color, double height, String material, String sound, String wingsState) {
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
