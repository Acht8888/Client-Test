package org.example.enums;

public enum FriendStatus {
    PENDING, ACCEPTED;

    public short toShort() {
        return (short) this.ordinal();
    }

    public static FriendStatus fromShort(short value) {
        return switch (value) {
            case 0 -> PENDING;
            case 1 -> ACCEPTED;
            default -> throw new IllegalArgumentException("Unexpected value: " + value);
        };
    }
}
