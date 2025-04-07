package com.viewton.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A DTO (Data Transfer Object) representing the final response for a query result.
 * <p>
 * This class encapsulates the list of entities returned from a query, the total value for specific attributes,
 * and the total count of matching entities. It also provides utility methods for accessing and working with
 * the query results in a convenient manner.
 * </p>
 *
 * @param <T> The type of the entities in the response.
 */
@Data
@RequiredArgsConstructor
public class ViewtonResponseDto<T> {
    private final List<T> list;
    private final List<T> sum;
    private final List<T> avg;
    private final Long count;

    public Optional<T> firstAvgResult() {
        if (this.avg != null) {
            return this.avg.stream().findFirst();
        }

        return Optional.empty();
    }

    public Stream<T> resultAvgStream() {
        if (this.avg != null) {
            return this.avg.stream();
        }

        return Stream.empty();
    }

    public Optional<T> findSumFirsValue() {
        if (this.sum != null) {
            return this.sum.stream().findFirst();
        }

        return Optional.empty();
    }

    public Stream<T> sumResultStream() {
        if (this.sum != null) {
            return this.sum.stream();
        }

        return Stream.empty();
    }

    public Optional<T> findListFirstResult() {
        if (this.list != null) {
            return this.list.stream().findFirst();
        }

        return Optional.empty();
    }

    public Stream<T> listResultStream() {
        if (this.list != null) {
            return this.list.stream();
        }

        return Stream.empty();
    }
}
