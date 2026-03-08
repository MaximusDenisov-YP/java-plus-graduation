package ru.yandex.practicum.kafka.serializer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {

    @Override
    public byte[] serialize(final String topic, final SpecificRecordBase data) {
        if (data == null) {
            return null;
        }

        try {
            Method toByteBufferMethod = data.getClass().getMethod("toByteBuffer");
            ByteBuffer buffer = (ByteBuffer) toByteBufferMethod.invoke(data);

            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return bytes;

        } catch (NoSuchMethodException e) {
            throw new SerializationException(
                    "Класс " + data.getClass().getName()
                            + " не поддерживает Avro single-object encoding для топика [" + topic + "]",
                    e
            );
        } catch (IllegalAccessException e) {
            throw new SerializationException(
                    "Нет доступа к методу toByteBuffer() для топика [" + topic + "]",
                    e
            );
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new SerializationException(
                    "Ошибка сериализации данных для топика [" + topic + "]",
                    cause instanceof IOException ? cause : e
            );
        }
    }
}