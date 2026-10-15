package com.reservation.model;

/** Static dropdown options for the reservation form. */
public final class Options {
    private Options() {}

    public static final String[] CLASS_TYPES = {
            "Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)", "AC First Class (1A)", "Chair Car (CC)"
    };

    public static final String[] STATIONS = {
            "Chennai Central", "Bengaluru City", "Hyderabad Deccan", "Mumbai CSMT",
            "New Delhi", "Kolkata Howrah", "Coimbatore Junction", "Madurai Junction",
            "Trivandrum Central", "Pune Junction"
    };
}
