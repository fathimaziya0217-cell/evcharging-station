package com.evcharging.exception;

public class SlotUnavailableException extends RuntimeException {
    public SlotUnavailableException(String message) {
        super(message);
    }
}
