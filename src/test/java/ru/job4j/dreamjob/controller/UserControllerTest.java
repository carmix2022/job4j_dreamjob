package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.UserService;
import ru.job4j.dreamjob.service.VacancyService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserService userService;
    private UserController userController;


    @BeforeEach
    public void initService() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegistrationPageThenGetOne() {
        var view = userController.getRegistationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserThenRedirectToLoginPage() {
        var testUser = new User(1, "email", "name", "password");
        when(userService.save(testUser)).thenReturn(Optional.of(testUser));

        var model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        var view = userController.register(testUser, model, request);

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRequestLoginPageThenGetOne() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenRedirectToVacanciesPage() {
        var testUser = new User(1, "email", "name", "password");
        when(userService.findByEmailAndPassword(testUser.getEmail(), testUser.getPassword())).thenReturn(Optional.of(testUser));

        var model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        var view = userController.loginUser(testUser, model, request);
        var actualUser = request.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(testUser);
    }

    @Test
    public void whenLoginUserFailedThenRedirectToLoginPageWithError() {
        var testUser = new User(1, "email", "name", "password");

        var model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        var view = userController.loginUser(testUser, model, request);
        var actualError = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualError).isEqualTo("Почта или пароль введены неверно");
    }

    @Test
    public void whenRequestLogoutThenGetOneAndRedirectToLoginPage() {
        HttpSession session = new MockHttpSession();
        var view = userController.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}