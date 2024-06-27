package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

public class SimpleVacancyService implements VacancyService {
    private static final SimpleVacancyService INSTANCE = new SimpleVacancyService();

    private final VacancyService vacancyService = SimpleVacancyService.getInstance();

    private SimpleVacancyService() { }

    public static SimpleVacancyService getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return vacancyService.save(vacancy);
    }

    @Override
    public boolean deleteById(int id) {
        return vacancyService.deleteById(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancyService.update(vacancy);
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyService.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyService.findAll();
    }
}
