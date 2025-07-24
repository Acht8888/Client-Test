package org.example.enums;

public enum RoomType {
    PUBLIC, PRIVATE;

    public short toShort() {
        return (short) this.ordinal();
    }

    public static RoomType fromShort(short value) {
        return switch (value) {
            case 0 -> PUBLIC;
            case 1 -> PRIVATE;
            default -> throw new IllegalArgumentException("Unexpected value: " + value);
        };
    }
}
