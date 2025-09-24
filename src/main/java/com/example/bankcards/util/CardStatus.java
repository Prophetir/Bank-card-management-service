package com.example.bankcards.util;

public enum CardStatus {
    BLOCKED("BLOCKED"),
    UNBLOCKED("UNBLOCKED"),
    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED"),
    ISSUED("ISSUED"),
    CLOSED("CLOSED"),
    DELETED("DELETED");

    private final String status;

    CardStatus(String status) { this.status = status; }

    public String toString() { return status; }
}
