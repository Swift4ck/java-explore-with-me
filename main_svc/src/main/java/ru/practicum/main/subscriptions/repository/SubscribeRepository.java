package ru.practicum.main.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.subscriptions.model.Subscription;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscription, Long> {

    public boolean existsBySubscriberIdAndTargetId(Long subscriberId, Long targetId);

    public List<Subscription> findBySubscriberId(Long subscriberId);

    public List<Subscription> findByTargetId(Long targetId);

    public void deleteBySubscriberIdAndTargetId(Long subscriberId, Long targetId);


}
