package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import static java.util.Optional.empty;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class
                .getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteByEmail(user.getEmail());
        }
    }

    @Test
    public void whenSaveAndGetSame() {
        var user = sql2oUserRepository.save(new User(0, "user1@mail.ru", "Ivan", "password")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSeveralUsersRegisteredThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "123@321.ru", "Ivan", "password")).get();
        var user2 = sql2oUserRepository.save(new User(0, "123@322.ru", "Sergei", "password")).get();
        var user3 = sql2oUserRepository.save(new User(0, "123@323.ru", "Alex", "password")).get();
        var rsl = sql2oUserRepository.findAll();
        assertThat(rsl).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenDeleteThenGetOptionalEmpty() {
        var user1 = sql2oUserRepository.save(new User(0, "123@321.ru", "Ivan", "password")).get();
        var isDeleted = sql2oUserRepository.deleteByEmail(user1.getEmail());
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(empty());
    }

    @Test
    public void whenEmailAlreadyExists() {
        var user1 = sql2oUserRepository.save(new User(0, "123@321.ru", "Ivan", "password"));
        var user2 = new User(0, "123@321.ru", "Ivan", "password");
        Optional<User> savedUser = sql2oUserRepository.save(user2);
        assertThat(savedUser).isEqualTo(empty());
    }
}