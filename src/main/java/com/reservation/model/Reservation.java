package com.reservation.model;

import java.time.LocalDate;

/** Immutable booking record. */
public record Reservation(
        String pnr,
        String passengerName,
        int trainNumber,
        String trainName,
        String classType,
        LocalDate journeyDate,
        String source,
        String destination) {

    /** Human-readable summary used in dialogs. */
    public String toDisplayString() {
        return """
                PNR Number     : %s
                Passenger Name : %s
                Train          : %d - %s
                Class          : %s
                Date of Journey: %s
                From           : %s
                To             : %s
                """.formatted(pnr, passengerName, trainNumber, trainName,
                classType, journeyDate, source, destination);
    }
}
