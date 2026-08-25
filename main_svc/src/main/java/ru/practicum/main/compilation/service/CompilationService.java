package ru.practicum.main.compilation.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    public ResponseEntity<Void> deleteCompilation(Long compId);

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    public List<CompilationDto> getCompilation(Boolean pinned, int from, int size);

    public CompilationDto getCompilationById(Long compId);

}
