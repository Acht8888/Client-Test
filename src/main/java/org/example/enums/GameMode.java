package org.example.enums;

public enum GameMode {
    CO_OP, PVP;

    public short toShort() {
        return (short) this.ordinal();
    }

    public static GameMode fromShort(short value) {
        return switch (value) {
            case 0 -> CO_OP;
            case 1 -> PVP;
            default -> throw new IllegalArgumentException("Unexpected value: " + value);
        };
    }
}
