package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
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
public class BaseTest  extends TestNGCitrusSpringSupport {
    @Autowired
    protected HttpClient yellowDuckService;

    @Autowired
    protected SingleConnectionDataSource yellowDuckDb;

    protected String generateQueryParametersString(String... parameters) {
        StringBuilder builder = new StringBuilder();
        String parameterName = null;
        boolean first = true;
        for(var parameter : parameters) {
            if(parameterName == null) {
                parameterName = parameter;
            } else {
                builder.append(first ? '?' : '&')
                        .append(parameterName).append('=').append(parameter);
                parameterName = null;
                first = false;
            }
        }
        return builder.toString();
    }

    protected void sendGetRequest(TestCaseRunner runner,
                                  HttpClient URL, String path) {
        runner.$(http().client(URL)
                .send()
                .get(path));
    }

    protected void sendDeleteRequest(TestCaseRunner runner,
                                     HttpClient URL, String path) {
        runner.$(http().client(URL)
                .send()
                .delete(path));
    }

    protected void sendPostRequest(TestCaseRunner runner,
                                   HttpClient URL, String path, String payload) {
        runner.$(http().client(URL)
                .send()
                .post(path)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload));
    }

    protected void sendPostRequest(TestCaseRunner runner,
                                   HttpClient URL, String path,
                                   String contentType, Object payload) {
        runner.$(http().client(URL)
                .send()
                .post(path)
                .message()
                .contentType(contentType)
                .body(new ObjectMappingPayloadBuilder(
                        payload,
                        new ObjectMapper())));
    }

    protected void sendPutRequest(TestCaseRunner runner,
                                  HttpClient URL, String path,
                                  String contentType, String payload) {
        runner.$(http().client(URL)
                .send()
                .put(path)
                .message()
                .contentType(contentType)
                .body(payload));
    }


    protected void receiveResponseAndValidate(TestCaseRunner runner,
                                              HttpClient client, HttpStatus status,
                                              String responseMessage,
                                              String path, String variable) {
        runner.$(http().client(client)
                .receive()
                .response(status)
                .message()
                .type(MessageType.JSON)
                .body(responseMessage)
                .extract(fromBody().expression(path, variable)));
    }


    protected void receiveResponseAndValidateWithPayload(TestCaseRunner runner,
                                                         HttpClient client, HttpStatus status,
                                                         Object expectedPayload,
                                                         String path, String variable) {
        runner.$(http().client(client)
                .receive()
                .response(status)
                .message()
                .type(MessageType.JSON)
                .body(new ObjectMappingPayloadBuilder(expectedPayload, new ObjectMapper()))
                .extract(fromBody().expression(path, variable)));
    }

    protected void receiveResponseAndValidateWithResource(TestCaseRunner runner,
                                                          HttpClient client, HttpStatus status,
                                                          String resourcePath,
                                                          String path, String variable) {
        runner.$(http().client(client)
                .receive()
                .response(status)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource(resourcePath))
                .extract(fromBody().expression(path, variable)
        ));
    }




    protected void sendDatabaseQueryAndValidate(TestCaseRunner runner, SingleConnectionDataSource dataSource,
                                                String statement, String... parameters) {
        var newQuery = query(dataSource).statement(statement);
        String parameterName = null;

        for (var parameter : parameters) {
            if (parameterName == null) {
                parameterName = parameter;
            } else {
                newQuery.validate(parameterName, parameter);
                parameterName = null;
            }
        }

        runner.$(newQuery);


    }

    protected void sendDatabaseQueryAndExtract(TestCaseRunner runner, SingleConnectionDataSource dataSource,
                                               String statement,
                                               String path, String variable) {
        runner.$(query(dataSource).statement(statement)
                .extract(path, variable));
    }
    protected void sendDatabaseRequest(TestCaseRunner runner, SingleConnectionDataSource dataSource,
                                                String statement) {
        runner.$(sql(dataSource)
                .statement(statement));
    }
}
