package io.twdps.starter.logging.logger;


import io.twdps.starter.logging.logger.setup.ClassWithLoggableEvent;
import io.twdps.starter.logging.logger.setup.LoggingTestConfig;
import io.twdps.starter.logging.logger.util.LogCapture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LoggingTestConfig.class)
public class LoggableEventTest {

    @Autowired
    private ClassWithLoggableEvent classWithLoggableEvent;

    private ByteArrayOutputStream loggingOutput;

    @BeforeClass
    public static void setLoggerContextSelector(){
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.selector.BasicContextSelector");
    }

    @Before
    public void setUp() {
        loggingOutput = LogCapture.captureLogOutput(LogEvent.class);
    }

    @After
    public void tearDown() {
        LogCapture.stopLogCapture(LogEvent.class, loggingOutput);
    }

    @Test
    public void shouldLogSuccessfulEventThatDoesntReturnStatusCode()  {
        classWithLoggableEvent.logSuccessFulEvent("DATA");

        String actualLog = new String(loggingOutput.toByteArray());
        assertThat(actualLog).contains("Successful call: io.twdps.starter.logging.logger.setup.ClassWithLoggableEvent.logSuccessFulEvent");
        assertThat(actualLog).contains("action=ACTION");
        assertThat(actualLog).contains("application_tier=SERVICE");
        assertThat(actualLog).contains("status=Success");
    }

    @Test
    public void shouldLogFailedEvent() {
        try {
            classWithLoggableEvent.logFailedEvent("DATA");
        } catch (Exception ignore) {
        }

        String actualLog = new String(loggingOutput.toByteArray());
        assertThat(actualLog).contains("Failed event");
        assertThat(actualLog).contains("action=ACTION");
        assertThat(actualLog).contains("application_tier=SERVICE");
        assertThat(actualLog).contains("status=Failed");
    }

    @Test
    public void shouldLogFailedEventIfStatusCodeReturnedIsServerError() throws Exception {
        classWithLoggableEvent.serverErrorStatusCode("DATA");

        String actualLog = new String(loggingOutput.toByteArray());
        assertThat(actualLog).contains("Status Code: 500");
        assertThat(actualLog).contains("Error: java.lang.Exception: something");
        assertThat(actualLog).contains("action=ACTION");
        assertThat(actualLog).contains("application_tier=CONTROLLER");
        assertThat(actualLog).contains("status=Failed");
    }

    @Test
    public void shouldLogFailedEventIfStatusCodeReturnedIsClientError() throws Exception {
        classWithLoggableEvent.clientErrorStatusCode("DATA");


        String actualLog = new String(loggingOutput.toByteArray());
        assertThat(actualLog).contains("Status Code: 400");
        assertThat(actualLog).contains("Error: java.lang.Error: wrong input");
        assertThat(actualLog).contains("action=ACTION");
        assertThat(actualLog).contains("application_tier=CONTROLLER");
        assertThat(actualLog).contains("status=Failed");
    }

    @Test
    public void shouldLogSuccessEventIfStatusCodeReturnedIsSuccess() throws Exception {
        classWithLoggableEvent.successStatusCode("DATA");

        String actualLog = new String(loggingOutput.toByteArray());
        assertThat(actualLog).contains("Successful call: io.twdps.starter.logging.logger.setup.ClassWithLoggableEvent.successStatusCode");
        assertThat(actualLog).contains("action=ACTION");
        assertThat(actualLog).contains("application_tier=CONTROLLER");
        assertThat(actualLog).contains("status=Success");
    }
}
