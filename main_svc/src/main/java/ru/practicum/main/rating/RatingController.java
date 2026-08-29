package ru.practicum.main.rating;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.rating.dto.CreateRatingDto;
import ru.practicum.main.rating.dto.OpinionDto;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.service.OpinionService;

@RestController
@AllArgsConstructor
public class RatingController {

    private final OpinionService opinionService;

    @PostMapping("/events/{eventId}/ratings")
    public OpinionDto createVoice(@PathVariable Long eventId, @RequestBody CreateRatingDto rating) {
        return opinionService.createVoice(eventId, rating);
    }

    @PatchMapping("/events/{eventId}/ratings")
    public OpinionDto updateVoice(@PathVariable Long eventId, @RequestBody CreateRatingDto rating) {
        return opinionService.updateVoice(eventId, rating);
    }

    @GetMapping("/{eventId}/rating")
    public RatingDto getRatingEvent(@PathVariable Long eventId) {
        return opinionService.getRatingEvent(eventId);
    }


}
