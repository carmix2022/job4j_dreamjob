package ru.job4j.dreamjob.controller;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    @Test
    public void whenRequestFileThenGetFile() throws IOException {
        FileService fileService = mock(FileService.class);
        FileController fileController = new FileController(fileService);
        MultipartFile testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});

        var testFileDto = new FileDto(testFile.getName(), testFile.getBytes());
        when(fileService.getFileById(1)).thenReturn(Optional.of(testFileDto));

        var returnedValue = fileController.getById(1);
        var expectedValue = ResponseEntity.ok(testFile.getBytes());

        assertThat(returnedValue).usingRecursiveComparison().isEqualTo(expectedValue);
    }

    @Test
    public void whenRequestFileThatIsAbsentThenGetNotFoundResponse() throws IOException {
        FileService fileService = mock(FileService.class);
        FileController fileController = new FileController(fileService);
        MultipartFile testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});

        var returnedValue = fileController.getById(anyInt());
        var expectedValue = ResponseEntity.notFound().build();

        assertThat(returnedValue).usingRecursiveComparison().isEqualTo(expectedValue);
    }
}