package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository
                .save(new User(0, "email1", "name1", "password"))
                .get();
        var savedUser = sql2oUserRepository
                .findByEmailAndPassword(user.getEmail(), user.getPassword())
                .get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }


    @Test
    public void whenSaveUserWithExcistingEmailThenGetFalse() {
        User user1 = new User(0, "email1", "name1", "password");
        User user2 = new User(1, "email1", "name2", "password2");
        var saveduser1 = sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);
        assertThat(saveduser1.get())
                .usingRecursiveComparison().isEqualTo(user1);
        assertThat(sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword()))
                .isEqualTo(empty());
    }
}