package ru.yandex.practicum.filmorate.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        if (duration != null) {
            long seconds = duration.toSeconds();
            jsonGenerator.writeNumber(seconds);
        } else {
            jsonGenerator.writeNull();
        }
    }
}
