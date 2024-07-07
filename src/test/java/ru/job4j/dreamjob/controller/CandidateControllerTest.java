package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.CandidateService;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class CandidateControllerTest {
    private CandidateService candidateService;
    private CandidateController candidateController;
    private FileService fileService;
    private CityService cityService;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        fileService = mock(FileService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService, fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var candidate2 = new Candidate(2, "test2", "desc2", now(), 3, 4);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.getAll(model, session);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var city3 = new City(2, "Екатеринбург");
        var expectedCities = List.of(city1, city2, city3);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.getCreationPage(model, session);
        var actualCandidate = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidate).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.create(candidate, testFile, model, session);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.create(new Candidate(), testFile, model, session);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenUpdateCandidateAndRedirectToCandidatesPage() {
        var candidate = new Candidate(1, "Test1", "desc1", now(), 1, 1);
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.update(candidate, testFile, model, session);
        var actualCandidates = candidateArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidates).isEqualTo(candidate);
    }

    @Test
    public void whenDeleteCandidateAndRedirectToCandidatesPage() {
        when(candidateService.deleteById(1)).thenReturn(true);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.delete(model, 1, session);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteCandidateAndThrownThenGetErrorPageWithMessage() {
        when(candidateService.deleteById(1)).thenReturn(false);

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.delete(model, 1, session);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenGetCandidateByIdThenReturnCandidatePage() {
        var candidate = new Candidate(1, "Test1", "desc1", now(), 1, 1);
        when(candidateService.findById(1)).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = candidateController.getById(model, 1, session);
        var actualCandidates = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidates).isEqualTo(candidate);
    }
}