package com.flash.alarm.domain.model;

public enum TaskStatus {
    SCHEDULED,
    CANCELLED,
    COMPLETED,
    FAILED;

    public static TaskStatus fromString(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown product status: " + value);
    }

}
