package andrew.volostnykh.viewton.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

// FIXME: to remove. Utils should be stored in separate library
@UtilityClass
public class DateUtil {

    public static LocalDateTime parseIsoDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateString, formatter);
    }

    public static LocalDate parseIsoDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        return LocalDate.parse(dateString, formatter);
    }
}

