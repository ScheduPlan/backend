package de.hofuniversity.assemblyplanner.util;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class DateUtil {
    public static Date toDate(Instant instant) {
        return new Date(instant.toEpochMilli());
    }

    public static Date addTemporalAmount(Date date, TemporalAmount duration) {
        return toDate(date.toInstant().plus(duration));
    }
}
