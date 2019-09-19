package cn.firstep.theflow.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingVO {
    @Min(value = 1, message = "{paging.page.min}")
    private int page = 1;

    @Min(value = 1, message = "{paging.size.min}")
    @Max(value = 300,  message = "{paging.size.max}")
    private int size = 10;

    //TODO support sort?
//	private String[] sort;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }
}
