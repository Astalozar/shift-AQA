package autotests.tests.duck_action_controller;

import autotests.clients.DuckActionsTest;
import autotests.payloads.ResponseSound;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.container.FinallySequence.Builder.doFinally;

@Epic("Тесты на duck-action-controller")
@Feature("Эндпоинт /api/duck/action/quack")

public class DuckActionQuackTest extends DuckActionsTest {
    @Test(description = "Проверить крякание уточки с нечетным ID и корректным звуком")
    @CitrusTest
    public void testOddCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckOdd,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckQuack(runner, duckIdVar, "2", "3");

        validateResponseWithPayload(runner, HttpStatus.OK, new ResponseSound().sound(
                "@equalsIgnoreCase('quack-quack-quack, quack-quack-quack')@"));
    }

    @Test(description = "Проверить крякание уточки с четным ID и корректным звуком")
    @CitrusTest
    public void testEvenCorrectQuack(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(doFinally().actions(
                context -> removeDuckTestData(runner)));

        createDuckTestData(runner, CheckEvenOdd.CheckEven,
                "yellow", "0.15", "wood", "quack", "FIXED");

        duckQuack(runner, duckIdVar, "3", "2");

        validateResponseWithPayload(runner, HttpStatus.OK, new ResponseSound().sound(
                "@equalsIgnoreCase('quack-quack, quack-quack, quack-quack')@"));
    }
}
