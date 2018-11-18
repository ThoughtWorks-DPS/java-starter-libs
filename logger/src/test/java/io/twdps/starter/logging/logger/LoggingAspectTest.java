package io.twdps.starter.logging.logger;

import io.twdps.starter.logging.logger.setup.Response;
import io.twdps.starter.logging.logger.setup.ClassWithLoggableEvent;
import io.twdps.starter.logging.logger.util.LogCapture;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.apache.logging.log4j.LogManager.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class LoggingAspectTest {

    @Mock
    private Signature signature;

    @Mock
    private ProceedingJoinPoint pjp;

    private ByteArrayOutputStream loggingOutput;

    private final String className = ClassWithLoggableEvent.class.getName();
    private final String methodName = "logSuccessFulEvent";
    private final String[] methodArgs = {"eom"};
    private Object methodResult = new Response();

    @BeforeClass
    public static void setLoggerContextSelector() {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.selector.BasicContextSelector");
    }

    @Before
    public void setUp() throws Exception {
        loggingOutput = LogCapture.captureLogOutput(LoggingAspect.class);
        System.setOut(new PrintStream(loggingOutput));

        Logger logger = (Logger) getLogger(LoggingAspect.class);
        logger.setLevel(Level.ALL);
    }

    @After
    public void tearDown() {
        LogCapture.stopLogCapture(LoggingAspect.class, loggingOutput);
    }

    @Test
    public void shouldLogMethodEntryAndExitWhenTargetMethodExitsNormally() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(className);
        when(signature.getName()).thenReturn(methodName);
        when(pjp.getArgs()).thenReturn(methodArgs);
        when(pjp.proceed()).thenReturn(methodResult);

        new LoggingAspect().logMethodEntryExit(pjp);

        String actualLog = new String(loggingOutput.toByteArray());
        String expectedEntryLog = String.format("Entering method %s.%s(%s)", className, methodName, String.join(",", methodArgs));
        String expectedExitLog = String.format("Exiting method %s.%s; execution time (ms):", className, methodName);
        String expectedExitLogResponse = String.format("response: %s;", methodResult);

        assertThat(actualLog, containsString(expectedEntryLog));
        assertThat(actualLog, containsString(expectedExitLog));
        assertThat(actualLog, containsString(expectedExitLogResponse));
    }

    @Test
    public void shouldLogMethodEntryWhenExceptionThrownByTargetMethod() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn(className);
        when(signature.getName()).thenReturn(methodName);
        when(pjp.getArgs()).thenReturn(methodArgs);
        when(pjp.proceed()).thenThrow(new RuntimeException());

        try {
            new LoggingAspect().logMethodEntryExit(pjp);
        } catch (Exception e) {
            //nothing to do
        }

        String actualLog = new String(loggingOutput.toByteArray());
        String expectedEntryLog = String.format("Entering method %s.%s(%s)", className, methodName, String.join(",", methodArgs));
        String expectedExitLog = String.format("Exiting method %s.%s; execution time (ms):", className, methodName);

        assertThat(actualLog, containsString(expectedEntryLog));
        assertThat(actualLog, not(containsString(expectedExitLog)));
    }
}
