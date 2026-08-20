package ru.practicum.main.compilation.dto;

import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.dto.EventMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(CompilationDto compilationDto) {

        Compilation compilation = new Compilation();

        compilation.setId(compilationDto.getId());
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());


        compilation.setEvents(compilationDto.getEvents().stream()
                .map(EventMapper::eventShortToEvent)
                .collect(Collectors.toList()));

        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();

        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        if (compilation.getEvents() != null) {
            dto.setEvents(compilation.getEvents().stream().map(EventMapper::toEventShortDto).collect(Collectors.toList()));
        } else {
            dto.setEvents(new ArrayList<>());
        }

        dto.setEvents(compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList()));

        return dto;
    }

}
