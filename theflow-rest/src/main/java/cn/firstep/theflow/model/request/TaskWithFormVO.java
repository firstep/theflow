package cn.firstep.theflow.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author Alvin4u
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskWithFormVO extends TaskVO {
    @Size(max = 255, message = "{form.outcome.max}")
    private String outcome;

    private Map<String, Object> formData;
}
