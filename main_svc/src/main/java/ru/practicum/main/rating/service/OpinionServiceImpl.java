package ru.practicum.main.rating.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.enums.Rating;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.rating.dto.CreateRatingDto;
import ru.practicum.main.rating.dto.OpinionDto;
import ru.practicum.main.rating.dto.OpinionMapper;
import ru.practicum.main.rating.dto.RatingDto;
import ru.practicum.main.rating.model.Opinion;
import ru.practicum.main.rating.repository.OpinionRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpinionServiceImpl implements OpinionService {

    private final OpinionRepository opinionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public OpinionDto createVoice(Long eventId, CreateRatingDto rating) {

        log.info("Запрос на создания голоса от пользователя {} для мероприятия {}", rating.getUserId(), eventId);

        if (rating.getUserId() == null || eventId == null) {
            throw new BadRequestException("Запрос составлен не верно");
        }

        if (rating.getRating() == null) {
            throw new BadRequestException("В запросе должна быть оценка");
        }

        User user = userRepository.findById(rating.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID:" + rating.getUserId() + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID:" + eventId + " не найден"));

        if (opinionRepository.existsByUserIdAndEventId(rating.getUserId(), eventId)) {
            throw new BadRequestException("Вы уже оставили голос за это мероприятие");
        }

        Opinion opinion = new Opinion();

        opinion.setRating(rating.getRating());
        opinion.setEventId(eventId);
        opinion.setUserId(rating.getUserId());
        opinion.setCreatedAt(LocalDateTime.now());

        Opinion saveOpinion = opinionRepository.save(opinion);

        return OpinionMapper.toOpinionDto(saveOpinion);
    }

    @Transactional
    @Override
    public OpinionDto updateVoice(Long eventId, CreateRatingDto rating) {

        if (rating.getUserId() == null || eventId == null) {
            throw new BadRequestException("Запрос составлен не верно");
        }

        if (rating.getRating() == null) {
            throw new BadRequestException("В запросе должна быть оценка");
        }

        User user = userRepository.findById(rating.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID:" + rating.getUserId() + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID:" + eventId + " не найден"));

        if (!opinionRepository.existsByUserIdAndEventId(rating.getUserId(), eventId)) {
            throw new BadRequestException("Вы ещё не оставили голос за это мероприятие");
        }

        Opinion opinion = opinionRepository.findByUserIdAndEventId(rating.getUserId(), eventId);

        if (opinion.getRating().equals(rating.getRating())) {
            throw new BadRequestException("Рейтинг такой же какой и был, если хотите поменять рейтинг, пожалуйста пришлите другой");
        }

        opinion.setRating(rating.getRating());

        Opinion saveOpinion = opinionRepository.save(opinion);

        return OpinionMapper.toOpinionDto(saveOpinion);
    }

    @Override
    public RatingDto getRatingEvent(Long eventId) {

        log.info("Получен запрос на получение рейтинга события {}", eventId);

        if (eventId == null) {
            throw new BadRequestException("Запрос составлен не верно");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Мероприятие с ID:" + eventId + " не найден"));

        Long countLike = opinionRepository.countByEventIdAndRating(eventId, Rating.LIKE);

        Long countDislike = opinionRepository.countByEventIdAndRating(eventId, Rating.DISLIKE);

        RatingDto ratingDto = new RatingDto();

        ratingDto.setEventId(eventId);
        ratingDto.setLikesCount(countLike);
        ratingDto.setDislikesCount(countDislike);
        ratingDto.setRatingScore(countLike - countDislike);
        ratingDto.setCountingTime(LocalDateTime.now());

        return ratingDto;
    }

}
