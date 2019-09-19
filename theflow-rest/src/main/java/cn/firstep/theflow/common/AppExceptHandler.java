package cn.firstep.theflow.common;

import cn.firstep.theflow.common.code.SystemCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global Exception Handler for REST.
 *
 * @author Alvin4u
 */
@RestControllerAdvice
public class AppExceptHandler {

    Logger logger = LoggerFactory.getLogger(AppExceptHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> serviceError(AppException exception) {
        return ResponseEntitys.of(exception);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> argsError(Exception e) {
        BindingResult result;
        if(e instanceof MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) e).getBindingResult();
        } else {
            result = ((BindException) e).getBindingResult();
        }

        Object[] messages = result.getAllErrors().stream().map(error -> error.getDefaultMessage()).toArray();

        return ResponseEntitys.of(SystemCode.BAD_REQUEST, StringUtils.join(messages, "; ") + ";");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> argsError(MethodArgumentTypeMismatchException e) {
        return ResponseEntitys.of(SystemCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> msgNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntitys.of(SystemCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> reqNotBinding(ServletRequestBindingException e) {
        return ResponseEntitys.of(SystemCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> mediaTypeNotSuppored(HttpMediaTypeNotSupportedException e) {
        return ResponseEntitys.of(SystemCode.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> noauthorization(AuthorizationException e) {
        return ResponseEntitys.of(SystemCode.UNAUTHORIZATION, e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> unknowError(Throwable e) {
        logger.error("Unknow error.", e);
        return ResponseEntitys.of(SystemCode.UNKNOW_ERROR, e.getMessage());
    }
}
