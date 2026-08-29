package ru.practicum.main.rating.service;

import ru.practicum.main.rating.dto.CreateRatingDto;
import ru.practicum.main.rating.dto.OpinionDto;
import ru.practicum.main.rating.dto.RatingDto;

public interface OpinionService {

    public OpinionDto createVoice(Long eventId, CreateRatingDto rating);

    public OpinionDto updateVoice(Long eventId, CreateRatingDto rating);

    public RatingDto getRatingEvent(Long eventId);

}
