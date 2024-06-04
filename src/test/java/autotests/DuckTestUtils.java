package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.dsl.MessageSupport.MessageBodySupport.fromBody;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckTestUtils {

    public enum CheckEvenOdd {
        NoCheck,
        CheckEven,
        CheckOdd
    }
    private static final String serverUrl = "http://localhost:2222";

    public static String getServerUrl() {
        return serverUrl;
    }

    private static final String duckIdTestVariableName ="duckId";
    public static String duckIdVarName() {
        return duckIdTestVariableName;
    }
    public static String duckId() {
        return "${" + duckIdTestVariableName +"}";
    }

    public static void createDuckTestData(TestCaseRunner runner, String duckIdVarName,
                                          String color, double height, String material, String sound, String wingsState) {
        runner.$(http().client(getServerUrl())
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" + "  \"color\": \"" + color + "\",\n"
                        + "  \"height\": " + height + ",\n"
                        + "  \"material\": \"" + material + "\",\n"
                        + "  \"sound\": \"" + sound + "\",\n"
                        + "  \"wingsState\": \"" + wingsState
                        + "\"\n" + "}"));
        runner.$(http().client(getServerUrl())
                .receive()
                .response(HttpStatus.OK)
                .message()
                .extract(fromBody().expression("$.id", duckIdVarName)));
    }

    public static void recreateDuckTestData(TestCaseRunner runner, TestContext context, CheckEvenOdd checkEvenOdd,
                                            String color, double height, String material, String sound, String wingsState) {
        try {
            int id = Integer.parseInt(context.getVariable(duckIdVarName()));
            if(checkEvenOdd == CheckEvenOdd.CheckEven && id % 2 != 0 ||
                    checkEvenOdd == CheckEvenOdd.CheckOdd && id % 2 == 0) {
                removeDuckTestData(runner, duckId());
                createDuckTestData(runner, duckId(), color, height, material, sound, wingsState);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void removeDuckTestData(TestCaseRunner runner, String duckIdVarName) {
        runner.$(http().client(getServerUrl())
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", duckIdVarName)
                .message());
        runner.$(http().client(getServerUrl())
                .receive()
                .response(HttpStatus.OK)
                .message());
    }

     public static void validateResponse(TestCaseRunner runner, HttpStatus status, String responseMessage) {
        runner.$(http().client(getServerUrl())
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(responseMessage));
    }
}
