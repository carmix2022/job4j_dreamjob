package ru.job4j.dreamjob.repository;

import net.bytebuddy.implementation.bytecode.Throw;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }
    @Override
    public Optional<User> save(User user) {
        Optional<User> addedUser = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO users(email, name, password)
                      VALUES (:email, :name, :password)
                      """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            try {
                int generatedId = query.executeUpdate().getKey(Integer.class);
                user.setId(generatedId);
            } catch (Sql2oException e) {
                if (e.getMessage()
                        .contains("повторяющееся значение ключа нарушает ограничение уникальности \"users_email_key\"")) {
                    return addedUser;
                }
            }
            addedUser = Optional.ofNullable(user);
            return addedUser;
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email AND password = :password");
            query.addParameter("email", email).addParameter("password", password);
            var user = query.executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
