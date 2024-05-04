package qinjus.productms.utils;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

public record HttpErrorInfo(ZonedDateTime timestamp, HttpStatus httpStatus,
                String path, String message) {
    
}
