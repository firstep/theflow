package cn.firstep.theflow.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response Object For Paging Data.
 *
 * @author Alvin4u
 */
@Getter
@Setter
public class PagingResponse<T> {

    private int page;

    private int size;

    private long total;

    private List<T> rows;

    @JsonInclude(Include.NON_NULL)
    private Object data;

    public static <T> PagingResponse<T> of(Page<T> page) {
        PagingResponse<T> resp = new PagingResponse<>();
        resp.setPage(page.getNumber() + 1);
        resp.setSize(page.getNumberOfElements());
        resp.setRows(page.getContent());
        resp.setTotal(page.getTotalElements());
        return resp;
    }

    public static <T> PagingResponse<T> of(int page, int size, long total, List<T> rows) {
        PagingResponse<T> resp = new PagingResponse<>();
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(total);
        resp.setRows(rows);
        return resp;
    }
}
