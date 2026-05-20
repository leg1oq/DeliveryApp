package com.delivery.model;

public enum StatusComanda {
    NOU("NOU"),
    IN_PREGATIRE("IN_PREGATIRE"),
    IN_LIVRARE("IN_LIVRARE"),
    LIVRAT("LIVRAT"),
    ANULAT("ANULAT");

    private final String dbValue;

    StatusComanda(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static StatusComanda fromString(String value) {
        for (StatusComanda s : values()) {
            if (s.dbValue.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Status necunoscut: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}
