package ru.practicum.main.compilation.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.model.UpdateCompilationRequest;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Запрос на создания подборки");

        if (newCompilationDto == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            for (Long id : newCompilationDto.getEvents()) {
                Event event = eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено"));
                events.add(event);
            }
        }

        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.isPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(events);

        Compilation save = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(save);
    }


    @Transactional
    @Override
    public ResponseEntity<Void> deleteCompilation(Long compId) {
        log.info("Получен запрос на удаления подборки с ID: {}", compId);

        Compilation deleteComp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id:" + compId + " не найдено"));

        compilationRepository.delete(deleteComp);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен запрос на обновление подборки с ID: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id:" + compId + " не найдено"));

        List<Event> events = new ArrayList<>();

        if (updateCompilationRequest.getEvents() != null && updateCompilationRequest.getEvents().isEmpty()) {
            for (Long id : updateCompilationRequest.getEvents()) {
                Event event = eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено"));
                events.add(event);
            }
        }
        compilation.setPinned(updateCompilationRequest.isPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());
        compilation.setEvents(events);

        Compilation save = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(save);
    }

    @Override
    public List<CompilationDto> getCompilation(Boolean pinned, int from, int size) {
        log.info("Получен запрос на получения подборки {} , {}", from, size);

        var pageable = PageRequest.of(from, size);

        var page = compilationRepository.findByPinned(pinned, pageable);


        return page.getContent().stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получен запрос на получение подборки по ID: {}", compId);

        if (compId == null) {
            throw new BadRequestException("Запрос составлен некорректно");
        }

        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id:" + compId + " не найдена"));

        return CompilationMapper.toCompilationDto(comp);
    }


}
