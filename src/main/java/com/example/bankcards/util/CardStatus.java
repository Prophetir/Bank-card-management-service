package com.example.bankcards.util;

public enum CardStatus {
    BLOCKED("BLOCKED"),
    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED"),
    ISSUED("ISSUED"),
    CLOSED("CLOSED");

    private final String status;

    CardStatus(String status) { this.status = status; }

    public String toString() { return status; }
}
