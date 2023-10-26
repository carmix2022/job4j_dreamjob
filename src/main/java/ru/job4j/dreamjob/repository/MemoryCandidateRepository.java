package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Petr", "description1", LocalDateTime.now().withNano(0)));
        save(new Candidate(0, "Ivan", "description2", LocalDateTime.now().plusDays(1).withNano(0)));
        save(new Candidate(0, "Sergey", "description3", LocalDateTime.now().plusDays(2).withNano(0)));
        save(new Candidate(0, "Vasily", "description4", LocalDateTime.now().plusDays(3).withNano(0)));
        save(new Candidate(0, "Alex", "description5", LocalDateTime.now().plusDays(4).withNano(0)));
        save(new Candidate(0, "Stepan", "description6", LocalDateTime.now().plusDays(5).withNano(0)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldVacancy) -> new Candidate(
                        oldVacancy.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

}