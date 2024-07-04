package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> save(User user);
    Optional<User> findByEmailAndPassword(String email, String password);
    boolean deleteByEmail(String email);
    Collection<User> findAll();
}
