package andrew.volostnykh.viewton.dto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The {@code ResultListDto} class is used for aggregation of query results and other information
 * @param <T> type of the result
 */
@Getter
@AllArgsConstructor
public class ResultListDto<T> {

    private final List<T> resultList;
    private final Long count;
    private final T total;

    public Optional<T> findFirstResult() {
        if (this.resultList != null) {
            return this.resultList.stream().findFirst();
        }

        return Optional.empty();
    }

    public Stream<T> resultStream() {
        if (this.resultList != null) {
            return this.resultList.stream();
        }

        return Stream.empty();
    }
}


