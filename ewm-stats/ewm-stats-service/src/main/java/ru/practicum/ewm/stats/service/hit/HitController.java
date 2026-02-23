package ru.practicum.ewm.stats.service.hit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.stats.dto.CreateHitDto;
import ru.practicum.ewm.stats.dto.GetStatsDto;
import ru.practicum.ewm.stats.dto.HitDto;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.service.hit.service.HitService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class HitController {
    private final HitService hitService;

    @PostMapping(path = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto createUser(@RequestBody @Valid CreateHitDto hitCreateDto) {
        return hitService.create(hitCreateDto);
    }

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto create(@RequestBody @Valid CreateHitDto hitCreateDto) {
        return hitService.create(hitCreateDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is before start date");
        }

        GetStatsDto getStatsDto = GetStatsDto.builder()
                .start(startDate)
                .end(endDate)
                .uris(uris)
                .unique(unique)
                .build();

        return hitService.getStats(getStatsDto);
    }

}
