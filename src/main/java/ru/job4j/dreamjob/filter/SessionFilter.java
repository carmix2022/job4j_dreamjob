package ru.job4j.dreamjob.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.job4j.dreamjob.model.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(2)
public class SessionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var session1 = request.getSession();
        addUserToSession(session1, request);
        chain.doFilter(request, response);
    }

    private void addUserToSession(HttpSession session1, HttpServletRequest request) {
        var user = (User) session1.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Гость");
        }
        request.setAttribute("user", user);
    }

}