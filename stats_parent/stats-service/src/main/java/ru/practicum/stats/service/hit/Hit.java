package ru.practicum.stats.service.hit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hits")
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app; //Идентификатор сервиса для которого записывается информация

    private String uri; //URI для которого был осуществлен запрос

    private String ip; //"IP-адрес пользователя, осуществившего запрос

    @Column(name = "hit_timestamp")
    private LocalDateTime timestamp; //Дата и время, когда был совершен запрос к эндпоинту (в формате \"yyyy-MM-dd HH:mm:ss\")


}
