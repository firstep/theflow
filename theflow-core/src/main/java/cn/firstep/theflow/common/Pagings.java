package cn.firstep.theflow.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Alvin4u
 */
public class Pagings {

    public static <T> Page<T> pack(List<T> content, Pageable pageable, long total) {
        if (content == null || content.isEmpty()) {
            return Page.empty(pageable);
        }
        return new PageImpl<>(content, pageable, total);
    }

}
