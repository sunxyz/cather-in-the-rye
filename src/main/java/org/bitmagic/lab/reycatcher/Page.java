package org.bitmagic.lab.reycatcher;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author yangrd
 * @date 2022/03/04
 */
public interface Page<T> {

    static <T> Page<T> of(List<T> content, Integer total) {
        return SimplePage.of(content, total);
    }

    List<T> getContent();

    Integer getTotal();

    @Value(staticConstructor = "of")
    class SimplePage<T> implements Page<T> {
        List<T> content;
        Integer total;
    }


}
