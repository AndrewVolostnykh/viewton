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
    private final T sum;
    private final Long count;

    public Optional<T> findFirstResult() {
        if (this.list != null) {
            return this.list.stream().findFirst();
        }

        return Optional.empty();
    }

    public Stream<T> resultStream() {
        if (this.list != null) {
            return this.list.stream();
        }

        return Stream.empty();
    }
}
