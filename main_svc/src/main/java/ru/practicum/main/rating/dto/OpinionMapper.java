package ru.practicum.main.rating.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.rating.model.Opinion;

@UtilityClass
public class OpinionMapper {

    public static OpinionDto toOpinionDto(Opinion opinion) {

        OpinionDto opinionDto = new OpinionDto();

        opinionDto.setRating(opinion.getRating());
        opinionDto.setCreatedAt(opinion.getCreatedAt());
        opinionDto.setEventId(opinion.getEventId());
        opinionDto.setUserId(opinion.getUserId());
        opinionDto.setId(opinion.getId());

        return opinionDto;
    }

}
