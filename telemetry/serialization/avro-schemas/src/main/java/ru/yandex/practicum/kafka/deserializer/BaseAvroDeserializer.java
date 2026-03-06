package ru.yandex.practicum.kafka.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

@Slf4j
public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DecoderFactory decoderFactory;
    private final DatumReader<T> datumReader;
    private BinaryDecoder decoder;

    public BaseAvroDeserializer(final org.apache.avro.Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(final DecoderFactory decoderFactory, final Schema schema) {
        this.decoderFactory = decoderFactory;
        this.datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(final String topic, final byte[] data) {
        if (data == null || data.length == 0) {
            log.warn("Нет данных для десериализации сообщения в топик {}", topic);
            return null;
        }
        try {
            decoder = decoderFactory.binaryDecoder(data, decoder);
            T result = datumReader.read(null, decoder);
            log.debug("Произведена десериализация сообщения в топик: {}", topic);
            return result;
        } catch (IOException e) {
            log.error("Произошла ошибка при десериализации ошибки в топик {}\nError: {}", topic, e.getMessage());
            throw new SerializationException(
                    String.format("Произошла ошибка при десериализации ошибки в топик %s\nError: %s", topic, e));
        }
    }
}
