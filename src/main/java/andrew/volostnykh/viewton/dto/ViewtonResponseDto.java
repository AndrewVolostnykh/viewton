package andrew.volostnykh.viewton.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Data
@RequiredArgsConstructor
public class ViewtonResponseDto<T> {
    private final List<T> list;
    private final T total;
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
