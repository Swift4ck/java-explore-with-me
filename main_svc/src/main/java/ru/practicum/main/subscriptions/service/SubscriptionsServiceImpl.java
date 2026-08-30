package ru.practicum.main.subscriptions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.event.dto.EventMapper;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.subscriptions.dto.SubscriptionDto;
import ru.practicum.main.subscriptions.dto.SubscriptionMapper;
import ru.practicum.main.subscriptions.model.Subscription;
import ru.practicum.main.subscriptions.repository.SubscribeRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionsServiceImpl implements SubscriptionsService {


    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final EventRepository eventRepository;


    @Transactional
    @Override
    public SubscriptionDto createSubscription(Long userId, Long targetId) {
        log.info("Запрос на подписку от профиля {}, на профиль {}", userId, targetId);


        if (userId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        if (targetId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        if (userId.equals(targetId)) {
            throw new BadRequestException("На себя подписываться нельзя(");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        User subscribe = userRepository.findById(targetId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + targetId));

        if (subscribeRepository.existsBySubscriberIdAndTargetId(userId, targetId)) {
            throw new BadRequestException("Вы уже подписаны на этого пользователя");
        }


        Subscription subscription = new Subscription();

        subscription.setSubscriberId(userId);
        subscription.setTargetId(targetId);
        subscription.setCreated(LocalDateTime.now());

        Subscription saveSub = subscribeRepository.save(subscription);

        return SubscriptionMapper.toSubscriptionDto(saveSub);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteSubscription(Long subscriberId, Long targetId) {
        log.info("Запрос на удаления подписки пользователя {} от {}", subscriberId, targetId);

        if (subscriberId == null || targetId == null) {
            throw new BadRequestException("ID пользователей не могут быть null");
        }

        if (!subscribeRepository.existsBySubscriberIdAndTargetId(subscriberId, targetId)) {
            throw new NotFoundException("Подписка не найдена: пользователь " + subscriberId + " не подписан на " + targetId);
        }

        subscribeRepository.deleteBySubscriberIdAndTargetId(subscriberId, targetId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public List<SubscriptionDto> getAllSubscribe(Long userId) {
        log.info("Запрос на получения подписок пользователя {}", userId);

        if (userId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        List<Subscription> subscriptions = subscribeRepository.findBySubscriberId(userId);

        return subscriptions.stream()
                .map(SubscriptionMapper::toSubscriptionDto)
                .toList();
    }

    @Override
    public List<SubscriptionDto> getAllSubscribers(Long userId) {
        log.info("Запрос на получения подписчиков пользователя {}", userId);

        if (userId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        List<Subscription> subscriptions = subscribeRepository.findByTargetId(userId);

        return subscriptions.stream()
                .map(SubscriptionMapper::toSubscriptionDto)
                .toList();
    }

    @Override
    public List<EventShortDto> eventsFeed(Long userId) {

        log.info("Запрос на получение списка ленты {}", userId);

        if (userId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        List<Subscription> subscriptions = subscribeRepository.findBySubscriberId(userId);

        if (subscriptions.isEmpty()) {
            log.debug("Список подписок пуст");
            return List.of();
        }

        List<Long> subscribeList = subscriptions.stream()
                .map(Subscription::getTargetId)
                .toList();

        List<Event> events = eventRepository.findByInitiatorIn(subscribeList);

        if (events.isEmpty()) {
            return List.of();
        }

        events.sort((e1, e2) -> e2.getEventDate().compareTo(e1.getEventDate()));

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }


}
