package pl.flaaaxxx.eskatebackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Logging {
    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }
}