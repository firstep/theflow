package cn.firstep.theflow.model.response;

import cn.firstep.theflow.common.AppCode;
import cn.firstep.theflow.common.AppException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response Object For Error Request.
 *
 * @author Alvin4u
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String code;

    @JsonInclude(Include.NON_NULL)
    private String message;

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static ErrorResponse of(AppException exception) {
        return of(exception.getCode(), exception.getMessage());
    }

    public static ErrorResponse of(AppCode code, String message) {
        return new ErrorResponse(code.category() + '.' + code.value(), message == null ? "" : message);
    }
}
