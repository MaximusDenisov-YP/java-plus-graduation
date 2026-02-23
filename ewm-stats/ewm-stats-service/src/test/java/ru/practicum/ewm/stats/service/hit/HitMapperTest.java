package ru.practicum.ewm.stats.service.hit;


import org.junit.jupiter.api.Test;
import ru.practicum.ewm.stats.dto.CreateHitDto;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.service.hit.mapper.HitMapper;
import ru.practicum.ewm.stats.service.hit.model.Hit;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapperTest {
    @Test
    void shouldConvertToDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse("2022-09-06 11:00:23", formatter);

        CreateHitDto dto = new CreateHitDto("ewm-main", "/event", "192.163.0.1", timestamp);
        Hit hit = HitMapper.toHit(dto);

        assertThat(hit.getApp()).isNotNull();
        assertThat(hit.getUri()).isNotNull();
        assertThat(hit.getIp()).isNotNull();
        assertThat(hit.getTimestamp()).isNotNull();
    }

    @Test
    void shouldConvertToEndPointHitDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp = LocalDateTime.parse("2022-09-06 11:00:23", formatter);

        Hit hit = new Hit(1L, "ewm-main", "/event", "192.163.0.1", timestamp);
        HitDto hitDto = HitMapper.toEndPointHitDto(hit);

        assertThat(hitDto.getId()).isEqualTo(hit.getId());
        assertThat(hitDto.getApp()).isEqualTo(hit.getApp());
        assertThat(hitDto.getUri()).isEqualTo(hit.getUri());
        assertThat(hitDto.getIp()).isEqualTo(hit.getIp());
        assertThat(hitDto.getTimestamp()).isEqualTo(hit.getTimestamp());
    }
}
