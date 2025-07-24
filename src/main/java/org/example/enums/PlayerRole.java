package org.example.enums;

public enum PlayerRole {
    LEADER, MEMBER;

    public short toShort() {
        return (short) this.ordinal();
    }

    public static PlayerRole fromShort(short value) {
        return switch (value) {
            case 0 -> LEADER;
            case 1 -> MEMBER;
            default -> throw new IllegalArgumentException("Unexpected value: " + value);
        };
    }
}
