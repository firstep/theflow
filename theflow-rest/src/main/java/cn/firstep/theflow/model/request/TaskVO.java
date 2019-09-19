package cn.firstep.theflow.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskVO {
    @Size(min=2, max = 255, message = "{task.opinion.size}")
    @NotBlank(message = "{task.opinion.required}")
    private String opinion;
    private Map<String, Object> variables;
}
