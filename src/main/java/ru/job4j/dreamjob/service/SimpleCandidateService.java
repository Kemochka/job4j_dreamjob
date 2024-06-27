package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

public class SimpleCandidateService implements CandidateService {
    private static final SimpleCandidateService INSTANCE = new SimpleCandidateService();
    private final CandidateService candidateService = SimpleCandidateService.getInstance();

    public SimpleCandidateService() {
    }

    public static SimpleCandidateService getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        return candidateService.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return candidateService.deleteById(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidateService.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateService.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateService.findAll();
    }
}
