package ru.practicum.main.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.category.Category;
import ru.practicum.main.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation; //краткое описание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "initiator_id")
    private Long initiator; //кто создал

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Boolean paid; //Нужно ли оплачивать участие

    private String title;//название

    private Integer confirmedRequests; //Количество одобренных заявок на участие в данном событии

    private LocalDateTime createdOn; //Дата и время создания события (в формате \"yyyy-MM-dd HH:mm:ss\")

    private String description;

    private Integer participantLimit = 0; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    private LocalDateTime publishedOn; //Дата и время публикации события (в формате \"yyyy-MM-dd HH:mm:ss\")

    private Boolean requestModeration = true; //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    private EventState state; //Список состояний жизненного цикла события

    private Long views; // просмотры

}
