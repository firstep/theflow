package cn.firstep.theflow.common;

import cn.firstep.theflow.model.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * ResponseEntity Wrapper.
 *
 * @author Alvin4u
 */
public class ResponseEntitys {

    public static ResponseEntity<?> of(AppException exception) {
        return ResponseEntity.status(exception.getCode().httpStatus(exception.getCode().value())).body(ErrorResponse.of(exception));
    }

    public static ResponseEntity<?> of(HttpStatus status) {
        return ResponseEntity.status(status).build();
    }

    public static <T> ResponseEntity<?> of(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(data);
    }

    public static ResponseEntity<?> of(AppCode code, String message) {
        return ResponseEntity.status(code.httpStatus(code.value())).body(ErrorResponse.of(code, message));
    }

    public static <T> ResponseEntity<?> of(T data, HttpHeaders headers) {
        return ResponseEntity.ok().headers(headers).body(data);
    }

    public static ResponseEntity<byte[]> of(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);

        return ResponseEntity.ok().headers(headers).body(data);
    }

    public static <T> ResponseEntity<?> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    public static ResponseEntity<?> created() {
        return of(HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<?> created(T data) {
        return of(HttpStatus.CREATED, data);
    }

    public static ResponseEntity<?> updated() {
        return of(HttpStatus.NO_CONTENT);
    }
}
