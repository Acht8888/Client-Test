package org.example.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BinarySerializer {

    public static byte[] serializeData(Object message) throws Exception {
        // Create a ByteArrayOutputStream and DataOutputStream for serialization
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        // Serialize the object and write it to the output stream
        BinarySerializer.serialize(dataOutputStream, message);

        // Get the serialized byte array
        byte[] serializedData = byteArrayOutputStream.toByteArray();

        // Close the streams
        dataOutputStream.close();
        byteArrayOutputStream.close();

        // Return the serialized byte data
        return serializedData;
    }

    public static <T> T deserializeData(byte[] serializedData, Class<T> clazz) throws Exception {
        // Create a ByteArrayInputStream and DataInputStream for deserialization
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        // Deserialize the object from the input stream
        T deserializedObject = BinarySerializer.deserialize(dataInputStream, clazz);

        // Close the streams
        dataInputStream.close();
        byteArrayInputStream.close();

        // Return the deserialized object
        return deserializedObject;
    }

    public static <T> void serialize(DataOutputStream out, T obj) throws Exception {
        for (Field field : getOrderedSerializableFields(obj.getClass())) {
            field.setAccessible(true);
            Object value = field.get(obj);
            writeValue(out, value, field.getType());
        }
    }

    public static <T> T deserialize(DataInputStream in, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : getOrderedSerializableFields(clazz)) {
            field.setAccessible(true);
            Object value = readValue(in, field.getType(), getGenericFieldType(field));
            field.set(obj, value);
        }
        return obj;
    }

    private static List<Field> getOrderedSerializableFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(FieldOrder.class)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        fields.sort(Comparator.comparingInt(f -> f.getAnnotation(FieldOrder.class).value()));
        return fields;
    }

    private static void writeValue(DataOutputStream out, Object value, Class<?> type) throws Exception {
        if (type == int.class || type == Integer.class) {
            out.writeInt(value != null ? (int) value : 0);
        } else if (type == short.class || type == Short.class) {
            out.writeShort(value != null ? (short) value : 0);
        } else if (type == float.class || type == Float.class) {
            out.writeFloat(value != null ? (float) value : 0f);
        } else if (type == long.class || type == Long.class) {
            out.writeLong(value != null ? (long) value : 0L);
        } else if (type == boolean.class || type == Boolean.class) {
            out.writeBoolean(value != null && (boolean) value);
        } else if (type == String.class) {  // Explicitly handle String type
            String str = (String) value;
            if (str != null) {
                out.writeShort((short)str.length());
                for (char ch : str.toCharArray()) {
                    out.writeChar(ch);
                }
            } else {
                out.writeShort(0);
            }
        } else if (type == UUID.class) {  // Handle UUID type
            UUID uuid = (UUID) value;
            out.writeShort((short)8);
            out.writeLong(uuid.getMostSignificantBits());  // Write the most significant bits
            out.writeLong(uuid.getLeastSignificantBits());  // Write the least significant bits
        } else if (type.isArray()) {
            Object[] array = (Object[]) value;
            int length = (array != null) ? array.length : 0;
            out.writeShort(length);
            Class<?> componentType = type.getComponentType();
            for (int i = 0; i < length; i++) {
                writeValue(out, array[i], componentType);
            }
        } else if (List.class.isAssignableFrom(type)) {
            List<?> list = (List<?>) value;
            int length = (list != null) ? list.size() : 0;
            out.writeShort(length);
            Class<?> elementType = Object.class;
            if (list != null && !list.isEmpty()) {
                elementType = list.get(0).getClass();
            }
            for (int i = 0; i < length; i++) {
                writeValue(out, list.get(i), elementType);
            }
        } else {
            if (value == null) {
                value = type.getDeclaredConstructor().newInstance(); // default object
            }
            serialize(out, value);
        }
    }


    private static Object readValue(DataInputStream in, Class<?> type, Type genericType) throws Exception {
        if (type == int.class || type == Integer.class)
            return in.readInt();
        if (type == short.class || type == Short.class)
            return in.readShort();
        if (type == float.class || type == Float.class)
            return in.readFloat();
        if (type == long.class || type == Long.class)
            return in.readLong();
        if (type == boolean.class || type == Boolean.class)
            return in.readBoolean();
        if (type == String.class) {  // Explicitly handle String type
            short length = in.readShort();  // Read the length of the string
            char[] chars = new char[length];
            for (int i = 0; i < length; i++) {
                chars[i] = in.readChar();  // Read each character of the string
            }
            return new String(chars);  // Convert the char array to a String and return it
        }
        if (type == UUID.class) {  // Handle UUID type
            in.readShort();
            long mostSigBits = in.readLong();  // Read the most significant bits of UUID
            long leastSigBits = in.readLong();  // Read the least significant bits of UUID
            return new UUID(mostSigBits, leastSigBits);  // Return the UUID object
        }
        if (type.isArray()) {
            short length = in.readShort();
            Class<?> elementType = type.getComponentType();
            Object array = Array.newInstance(elementType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, readValue(in, elementType, null));
            }
            return array;
        }

        if (List.class.isAssignableFrom(type)) {
            short length = in.readShort();
            Class<?> elementType = Object.class;
            if (genericType instanceof ParameterizedType) {
                Type actualType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                if (actualType instanceof Class) {
                    elementType = (Class<?>) actualType;
                }
            }
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                list.add(readValue(in, elementType, null));
            }
            return list;
        }

        return deserialize(in, type);
    }

    private static Type getGenericFieldType(Field field) {
        return field.getGenericType();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface FieldOrder {
        int value();
    }
}