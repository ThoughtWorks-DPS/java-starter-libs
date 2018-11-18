package io.twdps.starter.logging.logger.setup;

import io.twdps.starter.logging.logger.annotations.Loggable;
import io.twdps.starter.logging.logger.annotations.LoggableEvent;
import io.twdps.starter.logging.logger.domain.ApplicationTier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Loggable
public class ClassWithLoggableEvent {

    @LoggableEvent(applicationTier = ApplicationTier.SERVICE, action = "ACTION")
    public String logSuccessFulEvent(String data) {
        return data;
    }

    @LoggableEvent(applicationTier = ApplicationTier.SERVICE, action = "ACTION")
    public String logFailedEvent(String data) throws Exception {
        throw new Exception("Failed event");
    }

    @LoggableEvent(applicationTier = ApplicationTier.CONTROLLER, action = "ACTION")
    public ResponseEntity successStatusCode(String data) throws Exception {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @LoggableEvent(applicationTier = ApplicationTier.CONTROLLER, action = "ACTION")
    public ResponseEntity serverErrorStatusCode(String data) throws Exception {
        return new ResponseEntity<>(new Exception("something"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @LoggableEvent(applicationTier = ApplicationTier.CONTROLLER, action = "ACTION")
    public ResponseEntity clientErrorStatusCode(String data) throws Exception {
        return new ResponseEntity<>(new Error("wrong input"), HttpStatus.BAD_REQUEST);
    }
}
