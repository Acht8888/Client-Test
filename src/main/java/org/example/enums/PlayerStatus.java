package org.example.enums;

public enum PlayerStatus {
    READY, NOT_READY;

    public short toShort() {
        return (short) this.ordinal();
    }

    public static PlayerStatus fromShort(short value) {
        return switch (value) {
            case 0 -> READY;
            case 1 -> NOT_READY;
            default -> throw new IllegalArgumentException("Unexpected value: " + value);
        };
    }
}
