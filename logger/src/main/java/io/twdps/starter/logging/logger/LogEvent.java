package io.twdps.starter.logging.logger;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
public class LogEvent {
    public static final String FAILED = "Failed";
    public static final String SUCCESS = "Success";

    private String message;

    public void log() {
        log.info(this.message);
    }
}
