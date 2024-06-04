package autotests.duck_action_controller;

import autotests.DuckTestUtils;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckActionQuackTest extends TestNGCitrusSpringSupport {

    @Test(description = "QUACK ODD CORRECT SOUND")
    @CitrusTest
    public void testOddCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        duckQuackWithTestData(runner, DuckTestUtils.CheckEvenOdd.CheckOdd, 2, 2,
                "yellow", 0.15, "wood", "quack", "FIXED");
    }

    public void duckQuackWithTestData(TestCaseRunner runner, DuckTestUtils.CheckEvenOdd evenOdd,
                                           int repetitionCount, int soundCount,
                                           String color, double height, String material, String sound, String wingsState) {
        runner.$(doFinally().actions(
                context -> DuckTestUtils.removeDuckTestData(runner, DuckTestUtils.duckId())));

        DuckTestUtils.createDuckTestData(runner, DuckTestUtils.duckIdVarName(),
                color, height, material, sound, wingsState);
        if(evenOdd != DuckTestUtils.CheckEvenOdd.NoCheck) {
            run(context -> DuckTestUtils.recreateDuckTestData (
                    runner, context, evenOdd,
                    color, height, material, sound, wingsState));
        }

        duckQuack(runner, DuckTestUtils.duckId(), repetitionCount, soundCount);
        StringBuilder expectedSound = new StringBuilder();
        String quack = "quack";
        for(int i = 0; i < repetitionCount; i++) {
            for (int j = 0; j < soundCount; j++) {
                expectedSound.append(quack);
                if(j < soundCount - 1) {
                    expectedSound.append("-");
                }
            }
            if(i < repetitionCount - 1) {
                expectedSound.append(",");
            }
        }

        DuckTestUtils.validateResponse(runner, HttpStatus.OK,
                "{\n" +
                        "  \"sound\": \"@equalsIgnoreCase(" + expectedSound + ")@\"\n" +
                        "}");
    }

    public void duckQuack(TestCaseRunner runner, String id, String repetitionCount, String soundCount) {
        runner.$(http().client(DuckTestUtils.getServerUrl())
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", id)
                .queryParam("repetitionCount", repetitionCount)
                .queryParam("soundCount", soundCount));
    }
}
